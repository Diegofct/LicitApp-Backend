package com.elemental.licitapp.Sobre2.infrastructure.out.secop;

import com.elemental.licitapp.Exception.SecopApiException;
import com.elemental.licitapp.Sobre2.application.ports.out.OfertasSecopPort;
import com.elemental.licitapp.Sobre2.domain.entity.CriterioBusquedaOfertas;
import com.elemental.licitapp.Sobre2.domain.entity.OfertaImportada;
import com.elemental.licitapp.Sobre2.infrastructure.out.secop.dto.SecopOfertaDTO;
import com.elemental.licitapp.Sobre2.infrastructure.out.secop.mapper.SecopOfertaMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente del dataset "SECOPII - Ofertas Por Proceso" (Socrata / datos.gov.co).
 *
 * <p>No se reusa {@code SecopApiAdapter} porque apunta a otro dataset: p6dx-8zbt publica el
 * proceso con su adjudicatario, no la lista de oferentes. Aqui interesa la competencia
 * completa que abrio Sobre 2.
 */
@Component
public class SecopOfertasAdapter implements OfertasSecopPort {

    private static final Logger log = LoggerFactory.getLogger(SecopOfertasAdapter.class);

    private static final String SELECT_FIELDS = String.join(",",
            "identificador_de_la_oferta",
            "referencia_de_la_oferta",
            "referencia_del_proceso",
            "id_del_proceso_de_compra",
            "valor_de_la_oferta",
            "nombre_proveedor",
            "nit_del_proveedor",
            "entidad_compradora",
            "nit_entidad_compradora",
            "moneda",
            "modalidad",
            "fecha_de_registro");

    private final RestClient clienteActual;
    private final RestClient clienteHistorico;
    private final int limite;

    public SecopOfertasAdapter(@Value("${secop.ofertas.base-url}") String baseUrl,
                               @Value("${secop.ofertas.base-url-historico}") String baseUrlHistorico,
                               @Value("${secop.app-token}") String appToken,
                               @Value("${secop.ofertas.limite:2000}") int limite) {
        this.limite = limite;
        this.clienteActual = construir(baseUrl, appToken);
        this.clienteHistorico = construir(baseUrlHistorico, appToken);
    }

    private static RestClient construir(String baseUrl, String appToken) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-App-Token", appToken)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public List<OfertaImportada> buscarOfertas(CriterioBusquedaOfertas criterio, boolean historico) {
        String where = construirWhere(criterio);
        if (where == null) {
            return List.of();
        }

        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", SELECT_FIELDS)
                .queryParam("$where", where)
                // Sin paginacion a proposito: hay que traer todas las filas del proceso antes
                // de deduplicar, porque las filas de una misma oferta (una por integrante del
                // consorcio) no vienen necesariamente contiguas.
                .queryParam("$limit", limite)
                .build()
                .getQuery();

        RestClient cliente = historico ? clienteHistorico : clienteActual;
        log.info("➡️ Consultando ofertas del proceso en SECOP: ?{}", query);

        List<SecopOfertaDTO> filas;
        try {
            filas = cliente.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SecopOfertaDTO>>() {});
        } catch (Exception e) {
            throw new SecopApiException("No se pudieron obtener las ofertas del proceso desde SECOP.", e);
        }

        if (filas == null || filas.isEmpty()) {
            log.info("SECOP no devolvio ofertas para el criterio {}.", criterio);
            return List.of();
        }

        List<OfertaImportada> ofertas = deduplicar(filas);
        log.info("✅ SECOP devolvio {} filas -> {} ofertas distintas.", filas.size(), ofertas.size());
        return ofertas;
    }

    /**
     * En este dataset la referencia trae sufijos de fase, por ejemplo
     * {@code "LP-DEO-SVR-062-2025 (Fase de Selección (Presentación de ofertas))"}. Y en el
     * dataset de procesos (p6dx) a veces ya viene con sufijo tambien. Se corta en el primer
     * " (" para quedarse con el codigo del proceso y se busca por prefijo.
     *
     * <p>Solo aplica a la rama degradada: el cruce por identificador no necesita esto.
     */
    static String normalizarReferencia(String referencia) {
        if (referencia == null || referencia.isBlank()) {
            return null;
        }
        String limpia = referencia.trim();
        int corte = limpia.indexOf(" (");
        if (corte > 0) {
            limpia = limpia.substring(0, corte).trim();
        }
        return limpia.isEmpty() ? null : limpia;
    }

    /**
     * Dos formas de identificar el proceso, en orden de fiabilidad:
     *
     * <ol>
     *   <li><b>Por identificador</b> ({@code id_del_proceso_de_compra}): exacto. Este dataset
     *       usa el espacio {@code CO1.BDOS.*}, que no es el {@code CO1.REQ.*} de
     *       {@code p6dx.id_del_proceso}; el puente entre ambos es la columna
     *       {@code p6dx.id_del_portafolio}, que <b>si</b> es un {@code CO1.BDOS.*}. La
     *       cardinalidad medida es 1:1 y quien lo resuelve es {@code SecopProcesoAdapter}.
     *       Verificado extremo a extremo: {@code CO1.REQ.9138493 -> CO1.BDOS.8851669 -> 798
     *       filas -> 38 ofertas}, que coincide exacto con el
     *       {@code respuestas_al_procedimiento = 38} que publica p6dx.</li>
     *   <li><b>Por referencia</b> ({@code referencia_del_proceso like '<REF>%'} + NIT
     *       opcional): aproximado, solo para cuadros que no existen en SECOP. La referencia
     *       <b>no es unica</b>: {@code 'LP-001-2026'} aparece en 67 procesos de obra de
     *       entidades distintas. Ademas el {@code like} es prefix-match, asi que
     *       {@code LP-005-2026} tambien captura {@code LP-005-2026-B}. El {@code like} es
     *       inevitable en esta rama porque el dataset trae sufijos de fase.</li>
     * </ol>
     *
     * <p>Nota historica: una version anterior concluyo que "el join por identificador no
     * funciona" tras comparar {@code id_del_proceso_de_compra} contra {@code id_del_proceso}.
     * El hecho era cierto pero la conclusion no: faltaba {@code id_del_portafolio}.
     *
     * @return null cuando el criterio no trae con que buscar
     */
    private String construirWhere(CriterioBusquedaOfertas criterio) {
        return switch (criterio) {
            case CriterioBusquedaOfertas.PorIdDeProcesoDeCompra(String portafolio) ->
                    portafolio == null || portafolio.isBlank()
                            ? null
                            : String.format("id_del_proceso_de_compra = '%s'", escapar(portafolio.trim()));

            case CriterioBusquedaOfertas.PorReferencia(String ref, String nit) -> {
                String referencia = normalizarReferencia(ref);
                if (referencia == null) {
                    yield null;
                }
                StringBuilder where = new StringBuilder(String.format(
                        "referencia_del_proceso like '%s%%'", escapar(referencia)));
                if (nit != null && !nit.isBlank()) {
                    where.append(String.format(" AND nit_entidad_compradora = '%s'", escapar(soloDigitos(nit))));
                }
                yield where.toString();
            }
        };
    }

    /** SoQL escapa las comillas simples duplicandolas (mismo criterio que SecopApiAdapter). */
    private static String escapar(String valor) {
        return valor.replace("'", "''");
    }

    /** El NIT puede llegar con guion de verificacion o puntos; el dataset lo guarda numerico. */
    private static String soloDigitos(String nit) {
        String digitos = nit.replaceAll("\\D", "");
        return digitos.isEmpty() ? nit.trim() : digitos;
    }

    /**
     * Una misma oferta llega repetida, una fila por integrante del consorcio o UT: solo
     * cambia {@code c_digo_proveedor}. Medido: 798 filas para 38 oferentes en LIC-16787.
     * Sin esto las formulas se calcularian sobre la muestra inflada.
     *
     * <p>Cuando el identificador viene vacio se cae a "nombre + valor", que es lo mas
     * cercano a una identidad natural de la oferta.
     */
    private List<OfertaImportada> deduplicar(List<SecopOfertaDTO> filas) {
        Map<String, OfertaImportada> porOferta = new LinkedHashMap<>();
        for (SecopOfertaDTO fila : filas) {
            OfertaImportada oferta = SecopOfertaMapper.toDomain(fila);
            if (oferta == null || oferta.valorOferta() == null) {
                continue;
            }
            String clave = oferta.identificadorOferta() != null
                    ? oferta.identificadorOferta()
                    : oferta.nombreOferente() + "|" + oferta.valorOferta().toPlainString();
            porOferta.putIfAbsent(clave, oferta);
        }
        return new ArrayList<>(porOferta.values());
    }
}
