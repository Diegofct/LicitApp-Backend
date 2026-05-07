package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop;

import com.elemental.licitapp.Exception.SecopApiException;
import com.elemental.licitapp.Licitaciones.application.ports.out.SecopApiPort;
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
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Component
public class SecopApiAdapter implements SecopApiPort {

    private static final Logger log = LoggerFactory.getLogger(SecopApiAdapter.class);

    // Valor literal del dataset p6dx-8zbt (incluye la rareza ortográfica del proveedor).
    private static final String MODALIDAD_OBRA_PUBLICA = "Licitación pública Obra Publica";
    private static final String FECHA_FIELD = "fecha_de_publicacion_del";

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
    public Page<Licitacion> obtenerLicitacionesObraPublica(Pageable pageable) {
        int offset = pageable.getPageNumber() * pageable.getPageSize();
        String whereClause = buildWhereClause();

        List<Licitacion> licitaciones = fetchPage(whereClause, pageable.getPageSize(), offset);
        long totalElements = fetchCount(whereClause);

        return new PageImpl<>(licitaciones, pageable, totalElements);
    }

    private String buildWhereClause() {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        String oneYearAgoFormatted = oneYearAgo.format(DateTimeFormatter.ISO_LOCAL_DATE);
        return String.format(
                "estado_del_procedimiento = 'Publicado' "
                        + "AND modalidad_de_contratacion = '%s' "
                        + "AND %s >= '%sT00:00:00.000'",
                MODALIDAD_OBRA_PUBLICA, FECHA_FIELD, oneYearAgoFormatted);
    }

    private List<Licitacion> fetchPage(String whereClause, int limit, int offset) {
        String query = UriComponentsBuilder.newInstance()
                .queryParam("$select", SELECT_FIELDS)
                .queryParam("$where", whereClause)
                .queryParam("$order", FECHA_FIELD + " DESC")
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
}
