package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.elemental.licitapp.Licitaciones.application.ports.out.SecopApiPort;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopLicitacionDTO;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.mapper.SecopLicitacionMapper;
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
import java.util.Objects;
import java.util.StringJoiner;

@Component
public class SecopApiAdapter implements SecopApiPort {

    private static final Logger log = LoggerFactory.getLogger(SecopApiAdapter.class);

    private final RestClient restClient;
    private final String baseUrl;
    private final SecopLicitacionMapper mapper;

    public SecopApiAdapter(@Value("${secop.base-url}") String baseUrl,
                           @Value("${secop.app-token}") String appToken,
                           SecopLicitacionMapper mapper) {
        this.baseUrl = baseUrl;
        this.mapper = mapper;
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("X-App-Token", appToken)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Override
    public Page<Licitacion> obtenerLicitacionesPorModalidad(String modalidad, Pageable pageable) {
        int pageNumber = pageable.getPageNumber();
        int pageSize = pageable.getPageSize();
        log.debug("Pageable received: pageNumber={}, pageSize={}", pageNumber, pageSize);
        int offset = pageNumber * pageSize; // Pageable is 0-indexed for pageNumber
        log.debug("Calculated offset for SECOP API: {}", offset);

        String fechaField = "fecha_de_publicacion_del";

        StringJoiner selectFields = new StringJoiner(",")
                .add("id_del_proceso")
                .add("entidad")
                .add("descripci_n_del_procedimiento")
                .add("precio_base")
                .add("modalidad_de_contratacion")
                .add("tipo_de_contrato")
                .add("estado_del_procedimiento")
                .add("departamento_entidad")
                .add("ciudad_entidad")
                .add("urlproceso")
                .add("codigo_principal_de_categoria")
                .add("referencia_del_proceso")
                .add(fechaField + " AS fecha_publicacion_consolidada");

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        String oneYearAgoFormatted = oneYearAgo.format(DateTimeFormatter.ISO_LOCAL_DATE);

        String whereClause = String.format(
                "estado_del_procedimiento = 'Publicado' AND modalidad_de_contratacion = '%s' AND %s >= '%sT00:00:00.000'",
                modalidad,
                fechaField,
                oneYearAgoFormatted
        );

        // 1. Get the paginated data
        String dataUriQuery = UriComponentsBuilder.newInstance()
                .queryParam("$select", selectFields.toString())
                .queryParam("$where", whereClause)
                .queryParam("$order", fechaField + " DESC")
                .queryParam("$limit", pageSize)
                .queryParam("$offset", offset)
                .build()
                .getQuery();

        log.info("➡️ Llamando a SECOP para datos (v2 - GET): {}{}", baseUrl, "?" + dataUriQuery);

        List<SecopLicitacionDTO> dtos = Collections.emptyList();
        try {
            dtos = restClient.get()
                    .uri("?" + dataUriQuery)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<SecopLicitacionDTO>>() {});

            if (dtos == null) {
                log.info("✅ SECOP no respondió con registros para datos.");
                dtos = Collections.emptyList();
            } else {
                log.info("✅ SECOP respondió con {} registros.", dtos.size());
            }

        } catch (Exception e) {
            log.error("Error al llamar a la API de SECOP para datos: {}", e.getMessage(), e);
        }

        List<Licitacion> licitaciones = dtos.stream()
                .map(mapper::toEntity)
                .filter(licitacion -> licitacion.getFechaPublicacion() != null)
                .filter(licitacion -> licitacion.getUrlSecop() != null && !licitacion.getUrlSecop().contains("STS/Users/Login"))
                .toList();

        // 2. Get the total count
        long totalElements = 0;
        String countUriQuery = UriComponentsBuilder.newInstance()
                .queryParam("$select", "count(*)")
                .queryParam("$where", whereClause)
                .build()
                .getQuery();

        log.info("➡️ Llamando a SECOP para conteo total (v2 - GET): {}{}", baseUrl, "?" + countUriQuery);

        try {
            // The Secop API for count returns a list of maps, e.g., [{"count":"1234"}]
            List<Object> countResult = restClient.get()
                    .uri("?" + countUriQuery)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Object>>() {});

            if (countResult != null && !countResult.isEmpty()) {
                // Assuming the count is in the first element and is a map with a "count" key
                if (countResult.get(0) instanceof java.util.Map) {
                    java.util.Map<?, ?> countMap = (java.util.Map<?, ?>) countResult.get(0);
                    if (countMap.containsKey("count")) {
                        totalElements = Long.parseLong(Objects.toString(countMap.get("count")));
                    }
                }
            }
            log.info("✅ SECOP respondió con conteo total: {}", totalElements);

        } catch (Exception e) {
            log.error("Error al llamar a la API de SECOP para conteo total: {}", e.getMessage(), e);
        }

        return new PageImpl<>(licitaciones, pageable, totalElements);
    }
}
