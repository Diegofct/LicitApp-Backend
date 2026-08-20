package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop;

import com.elemental.licitapp.Exception.SecopApiException;
import com.elemental.licitapp.Licitaciones.application.ports.out.SecopApiPort;
import com.elemental.licitapp.Licitaciones.domain.entity.EstadoProceso;
import com.elemental.licitapp.Licitaciones.domain.entity.FiltroLicitaciones;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopLicitacionDTO;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.mapper.SecopLicitacionMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SecopApiAdapter implements SecopApiPort {

    private static final Logger log = LoggerFactory.getLogger(SecopApiAdapter.class);

    // Valor literal del dataset p6dx-8zbt (incluye la rareza ortográfica del proveedor).
    private static final String MODALIDAD_OBRA_PUBLICA = "Licitación pública Obra Publica";
    private static final String FECHA_FIELD = "fecha_de_publicacion_del";

    /** Filas a pedir para resolver el portafolio: el proceso viene repetido por fase. */
    private static final int FILAS_RESOLUCION_PORTAFOLIO = 10;

    /**
     * Filas a pedir para armar el estado del proceso. Muy por encima de las 10 anteriores y a
     * propósito: en los procesos por lotes SECOP publica el producto cruzado de ganadores por
     * valores, y uno de 18 lotes ya trae 324 filas. Quedarse corto perdería ganadores.
     */
    private static final int FILAS_ESTADO_PROCESO = 1000;

    private static final String FECHA_CIERRE_FIELD = "fecha_de_recepcion_de";
    private static final String DEPARTAMENTO_FIELD = "departamento_entidad";

    /** Lo que hace falta para armar el estado; deliberadamente más corto que el del listado. */
    private static final String SELECT_ESTADO_FIELDS = String.join(",",
            "fase",
            "estado_resumen",
            "estado_del_procedimiento",
            "urlproceso",
            "adjudicado",
            "respuestas_al_procedimiento",
            "numero_de_lotes",
            "fecha_de_ultima_publicaci",
            "nombre_del_proveedor",
            "valor_total_adjudicacion",
            "fecha_adjudicacion");

    private static final String SELECT_FIELDS = String.join(",",
            "id_del_proceso",
            "entidad",
            "descripci_n_del_procedimiento",
            "precio_base",
            "modalidad_de_contratacion",
            "tipo_de_contrato",
            "estado_del_procedimiento",
            "departamento_entidad",
            "ciudad_entidad",
            "urlproceso",
            "codigo_principal_de_categoria",
            "referencia_del_proceso",
            "fecha_de_recepcion_de",
            "duracion",
            "unidad_de_duracion",
            "id_del_portafolio",
            "fase",
            FECHA_FIELD + " AS fecha_publicacion_consolidada");

    private final RestClient restClient;
    private final String baseUrl;
    private final String appToken;
    private final SecopLicitacionMapper mapper;

    public SecopApiAdapter(@Value("${secop.base-url}") String baseUrl,
                           @Value("${secop.app-token}") String appToken,
                           SecopLicitacionMapper mapper) {
        this.baseUrl = baseUrl;
        this.appToken = appToken;
        this.mapper = mapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-App-Token", appToken)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @PostConstruct
    void validateToken() {
        if (appToken == null || appToken.isBlank() || appToken.startsWith("${")) {
            log.warn("⚠️ SECOP_APP_TOKEN no está configurado. Las consultas a SECOP usarán cuota de invitado y serán limitadas.");
        }
    }

    @Override
    public Page<Licitacion> obtenerLicitacionesObraPublica(Pageable pageable, FiltroLicitaciones filtro) {
        FiltroLicitaciones criterios = filtro != null ? filtro : FiltroLicitaciones.vacio();
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        String whereClause = buildWhereClause(criterios);

        List<Licitacion> licitaciones =
                fetchPage(whereClause, ordenDe(criterios), pageable.getPageSize(), offset);
        long totalElements = fetchCount(whereClause);

        return new PageImpl<>(licitaciones, pageable, totalElements);
    }

    /** Universo del módulo: obra pública publicada en el último año. Ningún filtro lo altera. */
    private String baseWhereClause() {
        String haceUnAno = LocalDate.now().minusYears(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
        return String.format(
                "estado_del_procedimiento = 'Publicado' "
                        + "AND modalidad_de_contratacion = '%s' "
                        + "AND %s >= '%sT00:00:00.000'",
                MODALIDAD_OBRA_PUBLICA, FECHA_FIELD, haceUnAno);
    }

    private String buildWhereClause(FiltroLicitaciones filtro) {
        StringBuilder where = new StringBuilder(baseWhereClause());

        // Filtro opcional por entidad (RF4): coincidencia parcial e insensible a
        // mayúsculas/minúsculas. Se escapan las comillas simples (SoQL las duplica)
        // para evitar inyección en la cláusula $where.
        if (tieneTexto(filtro.entidad())) {
            where.append(String.format(" AND upper(entidad) like upper('%%%s%%')",
                    escapar(filtro.entidad().trim())));
        }

        // Coincidencia exacta: el valor sale de obtenerDepartamentos(), que lo devuelve tal
        // como lo escribe SECOP, así que la ida y vuelta empareja sin normalizar acentos.
        if (tieneTexto(filtro.departamento())) {
            where.append(String.format(" AND %s = '%s'",
                    DEPARTAMENTO_FIELD, escapar(filtro.departamento().trim())));
        }

        // El presupuesto llega en pesos: precio_base es numérico y se compara sin comillas.
        if (filtro.presupuestoMin() != null) {
            where.append(" AND precio_base >= ").append(filtro.presupuestoMin().toPlainString());
        }
        if (filtro.presupuestoMax() != null) {
            where.append(" AND precio_base <= ").append(filtro.presupuestoMax().toPlainString());
        }

        // Cerca del 30% de lo publicado ya tiene la fecha de cierre vencida: es ruido para
        // quien está buscando a qué presentarse.
        if (filtro.soloVigentes()) {
            String hoy = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
            where.append(String.format(" AND %s >= '%sT00:00:00.000'", FECHA_CIERRE_FIELD, hoy));
        }

        // Ordenar por cierre dejando pasar las filas sin fecha las pondría a todas de primeras,
        // que es justo lo contrario de lo que se pide al elegir ese orden.
        if (filtro.orden() == FiltroLicitaciones.OrdenLicitaciones.CIERRE && !filtro.soloVigentes()) {
            where.append(String.format(" AND %s IS NOT NULL", FECHA_CIERRE_FIELD));
        }

        return where.toString();
    }

    private String ordenDe(FiltroLicitaciones filtro) {
        return filtro.orden() == FiltroLicitaciones.OrdenLicitaciones.CIERRE
                ? FECHA_CIERRE_FIELD + " ASC"
                : FECHA_FIELD + " DESC";
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    private List<Licitacion> fetchPage(String whereClause, String orden, int limit, int offset) {
        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", SELECT_FIELDS)
                .queryParam("$where", whereClause)
                .queryParam("$order", orden)
                .queryParam("$limit", limit)
                .queryParam("$offset", offset)
                .build()
                .getQuery();

        log.info("➡️ Consultando datos SECOP: {}?{}", baseUrl, query);

        List<SecopLicitacionDTO> dtos;
        try {
            dtos = restClient.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SecopLicitacionDTO>>() {});
        } catch (Exception e) {
            throw new SecopApiException("No se pudieron obtener las licitaciones desde SECOP.", e);
        }

        if (dtos == null) {
            return Collections.emptyList();
        }
        log.info("✅ SECOP devolvió {} registros.", dtos.size());
        return dtos.stream().map(mapper::toEntity).toList();
    }

    /**
     * p6dx devuelve el proceso repetido en varias filas (una por fase) y no todas traen el
     * portafolio, así que se piden unas pocas y se toma la primera que lo tenga. La relación
     * {@code id_del_proceso -> id_del_portafolio} es 1:1.
     */
    @Override
    public Optional<String> resolverPortafolio(String idDelProceso) {
        if (idDelProceso == null || idDelProceso.isBlank()) {
            return Optional.empty();
        }

        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", "id_del_portafolio")
                .queryParam("$where", "id_del_proceso = '" + escapar(idDelProceso.trim()) + "'")
                .queryParam("$limit", FILAS_RESOLUCION_PORTAFOLIO)
                .build()
                .getQuery();

        List<SecopLicitacionDTO> filas;
        try {
            filas = restClient.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SecopLicitacionDTO>>() {});
        } catch (Exception e) {
            throw new SecopApiException(
                    "No se pudo resolver el portafolio del proceso " + idDelProceso + " en SECOP.", e);
        }

        if (filas == null) {
            return Optional.empty();
        }
        return filas.stream()
                .map(SecopLicitacionDTO::getIdDelPortafolio)
                .filter(portafolio -> portafolio != null && !portafolio.isBlank())
                .findFirst();
    }

    /**
     * Fase y desenlace del proceso. p6dx devuelve una fila por fase —y los procesos por lotes,
     * una por lote adjudicado—, así que se piden todas y se consolidan aquí: el estado sale de
     * la publicación más reciente y las adjudicaciones son la unión de las filas ya resueltas.
     */
    @Override
    public Optional<EstadoProceso> resolverEstadoProceso(String idDelProceso) {
        if (idDelProceso == null || idDelProceso.isBlank()) {
            return Optional.empty();
        }

        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", SELECT_ESTADO_FIELDS)
                .queryParam("$where", "id_del_proceso = '" + escapar(idDelProceso.trim()) + "'")
                .queryParam("$order", "fecha_de_ultima_publicaci DESC")
                .queryParam("$limit", FILAS_ESTADO_PROCESO)
                .build()
                .getQuery();

        List<SecopLicitacionDTO> filas;
        try {
            filas = restClient.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SecopLicitacionDTO>>() {});
        } catch (Exception e) {
            throw new SecopApiException(
                    "No se pudo resolver el estado del proceso " + idDelProceso + " en SECOP.", e);
        }

        if (filas == null || filas.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(consolidarEstado(idDelProceso.trim(), filas));
    }

    private EstadoProceso consolidarEstado(String idDelProceso, List<SecopLicitacionDTO> filas) {
        SecopLicitacionDTO masReciente = filas.get(0);

        EstadoProceso estado = new EstadoProceso();
        estado.setIdDelProceso(idDelProceso);
        estado.setFase(masReciente.getFase());
        estado.setEstadoResumen(masReciente.getEstadoResumen());
        estado.setEstadoDelProcedimiento(masReciente.getEstado());
        estado.setNumeroDeLotes(masReciente.getNumeroDeLotes());
        estado.setFechaUltimaPublicacion(aFecha(masReciente.getFechaUltimaPublicacion()));

        estado.setUrl(filas.stream()
                .map(fila -> mapper.extractUrl(fila.getUrlProceso()))
                .filter(url -> url != null && !url.isBlank())
                .findFirst()
                .orElse(null));

        // El conteo de ofertas solo crece y no todas las filas del proceso lo traen al día:
        // el máximo es más fiable que confiar en la primera.
        estado.setNumeroDeOferentes(filas.stream()
                .map(SecopLicitacionDTO::getRespuestasAlProcedimiento)
                .filter(Objects::nonNull)
                .max(Integer::compareTo)
                .orElse(null));

        estado.setAdjudicado(filas.stream().anyMatch(SecopApiAdapter::estaAdjudicada));

        boolean porLotes = masReciente.getNumeroDeLotes() != null && masReciente.getNumeroDeLotes() > 1;
        estado.setAdjudicacionPorLotes(porLotes);
        estado.setAdjudicaciones(extraerAdjudicaciones(filas, porLotes));

        return estado;
    }

    /**
     * Ganadores del proceso. Se separan los dos casos porque el dato de SECOP no es el mismo:
     *
     * <ul>
     *   <li><b>Lote único:</b> una fila por adjudicación, así que proveedor y valor van juntos
     *       y el par es fiable. Se deduplica por la terna completa, porque el 7% de los
     *       procesos repite filas idénticas.</li>
     *   <li><b>Por lotes:</b> SECOP publica el producto cruzado de ganadores por valores y no
     *       hay columna de lote, así que el par proveedor-valor no existe en el dato. Se
     *       deduplica solo por proveedor y el valor se deja en null en vez de atribuirle uno
     *       cualquiera de los del proceso.</li>
     * </ul>
     */
    private List<EstadoProceso.Adjudicacion> extraerAdjudicaciones(List<SecopLicitacionDTO> filas,
                                                                   boolean porLotes) {
        List<SecopLicitacionDTO> adjudicadas = filas.stream()
                .filter(SecopApiAdapter::estaAdjudicada)
                .filter(fila -> fila.getNombreDelProveedor() != null
                        && !fila.getNombreDelProveedor().isBlank())
                .toList();

        if (!porLotes) {
            return new ArrayList<>(adjudicadas.stream()
                    .map(fila -> new EstadoProceso.Adjudicacion(
                            fila.getNombreDelProveedor().trim(),
                            fila.getValorTotalAdjudicacion(),
                            aFecha(fila.getFechaAdjudicacion())))
                    .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        Map<String, LocalDate> fechaPorProveedor = new LinkedHashMap<>();
        for (SecopLicitacionDTO fila : adjudicadas) {
            fechaPorProveedor.putIfAbsent(
                    fila.getNombreDelProveedor().trim(), aFecha(fila.getFechaAdjudicacion()));
        }
        return fechaPorProveedor.entrySet().stream()
                .map(entrada -> new EstadoProceso.Adjudicacion(entrada.getKey(), null, entrada.getValue()))
                .toList();
    }

    /** SECOP publica el indicador como texto "Si"/"No", no como booleano. */
    private static boolean estaAdjudicada(SecopLicitacionDTO fila) {
        String adjudicado = fila.getAdjudicado();
        return adjudicado != null && "Si".equalsIgnoreCase(adjudicado.trim());
    }

    /** SECOP entrega estas fechas siempre a las 00:00: el día es el único dato real. */
    private static LocalDate aFecha(LocalDateTime fechaHora) {
        return fechaHora != null ? fechaHora.toLocalDate() : null;
    }

    /**
     * Departamentos presentes en el universo del módulo. Se agrupan en la API en vez de traer
     * todas las filas para contarlas aquí.
     */
    @Override
    public List<String> obtenerDepartamentos() {
        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", DEPARTAMENTO_FIELD)
                .queryParam("$where", baseWhereClause())
                .queryParam("$group", DEPARTAMENTO_FIELD)
                .queryParam("$order", DEPARTAMENTO_FIELD)
                .build()
                .getQuery();

        List<DepartamentoResponse> filas;
        try {
            filas = restClient.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<DepartamentoResponse>>() {});
        } catch (Exception e) {
            throw new SecopApiException("No se pudieron obtener los departamentos desde SECOP.", e);
        }

        if (filas == null) {
            return List.of();
        }
        return filas.stream()
                .map(DepartamentoResponse::departamento)
                .filter(departamento -> departamento != null && !departamento.isBlank())
                .toList();
    }

    /** SoQL escapa la comilla simple duplicándola. */
    private String escapar(String valor) {
        return valor.replace("'", "''");
    }

    private long fetchCount(String whereClause) {
        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", "count(*)")
                .queryParam("$where", whereClause)
                .build()
                .getQuery();

        log.info("➡️ Consultando conteo SECOP: {}?{}", baseUrl, query);

        List<CountResponse> result;
        try {
            result = restClient.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<CountResponse>>() {});
        } catch (Exception e) {
            throw new SecopApiException("No se pudo obtener el conteo total de licitaciones desde SECOP.", e);
        }

        if (result == null || result.isEmpty() || result.get(0).count() == null) {
            return 0L;
        }
        try {
            return Long.parseLong(result.get(0).count());
        } catch (NumberFormatException e) {
            log.warn("Conteo SECOP no parseable: {}", result.get(0).count());
            return 0L;
        }
    }

    private record CountResponse(@JsonProperty("count") String count) {}

    private record DepartamentoResponse(
            @JsonProperty("departamento_entidad") String departamento) {}
}
