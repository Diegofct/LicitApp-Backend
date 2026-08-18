package com.elemental.licitapp.Sobre2.infrastructure.out.secop;

import com.elemental.licitapp.Exception.SecopApiException;
import com.elemental.licitapp.Sobre2.application.ports.out.ProcesoSecopPort;
import com.elemental.licitapp.Sobre2.domain.entity.ProcesoSecop;
import com.elemental.licitapp.Sobre2.infrastructure.out.secop.dto.SecopProcesoDTO;
import com.elemental.licitapp.Sobre2.infrastructure.out.secop.mapper.SecopProcesoMapper;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Cliente del dataset "SECOP II - Procesos de Contratacion" (p6dx-8zbt), usado unicamente
 * para resolver la identidad de un proceso.
 *
 * <p>No se reusa {@code SecopApiAdapter} del slice Licitaciones aunque apunte al mismo
 * dataset: su puerto expone un catalogo paginado de licitaciones de obra publica, no una
 * busqueda puntual por id, y su {@code $select} no pide {@code id_del_portafolio}. Ampliarlo
 * obligaria a redefinir un puerto de otro slice para un dato que a ese slice no le sirve.
 */
@Component
public class SecopProcesoAdapter implements ProcesoSecopPort {

    private static final Logger log = LoggerFactory.getLogger(SecopProcesoAdapter.class);

    private static final String SELECT_FIELDS = String.join(",",
            "id_del_proceso",
            "id_del_portafolio",
            "nit_entidad",
            "referencia_del_proceso",
            "precio_base");

    /**
     * p6dx devuelve el proceso en varias filas. La relacion {@code id_del_proceso ->
     * id_del_portafolio} es 1:1, asi que unas pocas filas bastan para hallar el portafolio.
     */
    private static final int LIMITE_FILAS = 10;

    private final RestClient cliente;

    public SecopProcesoAdapter(@Value("${secop.base-url}") String baseUrl,
                               @Value("${secop.app-token}") String appToken) {
        this.cliente = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-App-Token", appToken)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Optional<ProcesoSecop> resolver(String idDelProceso) {
        if (idDelProceso == null || idDelProceso.isBlank()) {
            return Optional.empty();
        }

        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", SELECT_FIELDS)
                .queryParam("$where", "id_del_proceso = '" + escapar(idDelProceso.trim()) + "'")
                .queryParam("$limit", LIMITE_FILAS)
                .build()
                .getQuery();

        log.info("➡️ Resolviendo proceso en SECOP: ?{}", query);

        List<SecopProcesoDTO> filas;
        try {
            filas = cliente.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SecopProcesoDTO>>() {});
        } catch (Exception e) {
            throw new SecopApiException(
                    "No se pudo resolver el proceso " + idDelProceso + " en SECOP.", e);
        }

        if (filas == null || filas.isEmpty()) {
            log.info("SECOP no devolvio ninguna fila para el proceso '{}'.", idDelProceso);
            return Optional.empty();
        }

        List<ProcesoSecop> procesos = filas.stream()
                .map(SecopProcesoMapper::toDomain)
                .filter(Objects::nonNull)
                .toList();

        // Se prefiere una fila con portafolio; si ninguna lo trae se devuelve la primera de
        // todas modos, porque su NIT sirve para acotar la busqueda degradada por referencia.
        return procesos.stream()
                .filter(ProcesoSecop::tienePortafolio)
                .findFirst()
                .or(() -> procesos.stream().findFirst());
    }

    /**
     * El conteo se busca por <b>portafolio</b> y no por {@code id_del_proceso} porque un
     * mismo proceso tiene varias filas, una por fase, y solo la de "Fase de Seleccion"
     * trae el numero real.
     *
     * <p>Caso medido (LP-005-2026 del municipio de Samaca, {@code CO1.BDOS.10331610}):
     * la fila de "Presentacion de observaciones" dice {@code respuestas_al_procedimiento
     * = 0} y la de "Fase de Seleccion" dice {@code 23}. Por eso hay que quedarse con el
     * <b>maximo</b>: leer una fila cualquiera daria cero y haria creer que nadie se presento.
     *
     * <p>El maximo se calcula <b>en el servidor</b> y no trayendo filas, porque un proceso
     * puede tener muchas: {@code CO1.BDOS.8851669} tiene 100. Con un {@code $limit} se
     * estaria muestreando y el maximo real podria quedar fuera. El {@code ::number} es
     * necesario porque la columna es de tipo texto y sin el cast el maximo seria
     * lexicografico ("9" > "23").
     */
    @Override
    public Optional<Integer> contarProponentes(String idDelPortafolio) {
        if (idDelPortafolio == null || idDelPortafolio.isBlank()) {
            return Optional.empty();
        }

        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", "max(respuestas_al_procedimiento::number) as maximo")
                .queryParam("$where", "id_del_portafolio = '" + escapar(idDelPortafolio.trim()) + "'")
                .build()
                .getQuery();

        List<MaximoResponse> filas;
        try {
            filas = cliente.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<MaximoResponse>>() {});
        } catch (Exception e) {
            // El conteo es informativo: si falla, no vale la pena tumbar la importacion.
            log.warn("No se pudo contar los proponentes de {}: {}", idDelPortafolio, e.getMessage());
            return Optional.empty();
        }

        if (filas == null || filas.isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(SecopProcesoMapper.aEntero(filas.get(0).maximo()));
    }

    /** Socrata devuelve los agregados como texto, igual que el resto de columnas. */
    private record MaximoResponse(@JsonProperty("maximo") String maximo) {
    }

    /** SoQL escapa las comillas simples duplicandolas (mismo criterio que el resto de adaptadores). */
    private static String escapar(String valor) {
        return valor.replace("'", "''");
    }
}
