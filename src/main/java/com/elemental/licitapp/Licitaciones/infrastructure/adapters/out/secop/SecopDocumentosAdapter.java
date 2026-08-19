package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop;

import com.elemental.licitapp.Exception.SecopApiException;
import com.elemental.licitapp.Licitaciones.application.ports.out.DocumentosSecopPort;
import com.elemental.licitapp.Licitaciones.domain.entity.DocumentoProceso;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopDocumentoDTO;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.mapper.SecopDocumentoMapper;
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

/**
 * Cliente del dataset "SECOP II - Archivos Descarga" (dmgg-8hin), que lista los documentos
 * publicados en cada proceso junto con su enlace de descarga.
 *
 * <p>El cruce se hace por {@code proceso}, que es el identificador de portafolio
 * ({@code CO1.BDOS.*}) y no el {@code id_del_proceso}: el mismo criterio que ya usa Sobre 2
 * para las ofertas.
 *
 * <p>El dataset cubre lo publicado desde 2025. Alcanza porque el catálogo de licitaciones se
 * limita al último año; para ventanas más largas habría que consultar además los datasets
 * históricos por vigencia (nbae-kzan 2024, 3skv-9na7 2023, kgcd-kt7i 2022, f8va-cf4m ≤2021).
 */
@Component
public class SecopDocumentosAdapter implements DocumentosSecopPort {

    private static final Logger log = LoggerFactory.getLogger(SecopDocumentosAdapter.class);

    private static final String SELECT_FIELDS = String.join(",",
            "id_documento",
            "proceso",
            "nombre_archivo",
            "extensi_n",
            "descripci_n",
            "tamanno_archivo",
            "fecha_carga",
            "url_descarga_documento");

    private final RestClient cliente;
    private final int limite;

    public SecopDocumentosAdapter(@Value("${secop.documentos.base-url}") String baseUrl,
                                  @Value("${secop.app-token}") String appToken,
                                  @Value("${secop.documentos.limite}") int limite) {
        this.limite = limite;
        this.cliente = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-App-Token", appToken)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public List<DocumentoProceso> obtenerDocumentos(String idDelPortafolio) {
        if (idDelPortafolio == null || idDelPortafolio.isBlank()) {
            return List.of();
        }

        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", SELECT_FIELDS)
                .queryParam("$where", "proceso = '" + escapar(idDelPortafolio.trim()) + "'")
                .queryParam("$limit", limite)
                .build()
                .getQuery();

        log.info("➡️ Consultando documentos del proceso en SECOP: ?{}", query);

        List<SecopDocumentoDTO> filas;
        try {
            filas = cliente.get()
                    .uri("?" + query)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SecopDocumentoDTO>>() {});
        } catch (Exception e) {
            throw new SecopApiException(
                    "No se pudieron obtener los documentos del proceso " + idDelPortafolio + " desde SECOP.", e);
        }

        if (filas == null || filas.isEmpty()) {
            log.info("SECOP no reporta documentos para el portafolio '{}'.", idDelPortafolio);
            return List.of();
        }

        return filas.stream()
                .map(SecopDocumentoMapper::toDomain)
                .filter(Objects::nonNull)
                .filter(documento -> documento.getUrl() != null)
                .toList();
    }

    /** SoQL escapa la comilla simple duplicándola, igual que el resto de adaptadores SECOP. */
    private String escapar(String valor) {
        return valor.replace("'", "''");
    }
}
