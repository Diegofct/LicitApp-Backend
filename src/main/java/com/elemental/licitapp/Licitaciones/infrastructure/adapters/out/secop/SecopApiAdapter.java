package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop;

import com.elemental.licitapp.Licitaciones.application.ports.out.SecopApiPort;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopLicitacionDTO;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.mapper.SecopLicitacionMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

@Component
public class SecopApiAdapter implements SecopApiPort {

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
    public List<Licitacion> obtenerProcesosActivos(int pageNumber, int pageSize) {
        int offset = (pageNumber - 1) * pageSize;

        // Usamos 'fecha_de_publicacion_del' que es el campo correcto para la fecha de publicación.
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
                // Mapeamos el campo de fecha a nuestro alias para el DTO.
                .add(fechaField + " AS fecha_publicacion_consolidada");

        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        String oneYearAgoFormatted = oneYearAgo.format(DateTimeFormatter.ISO_LOCAL_DATE);

        String whereClause = String.format(
                "estado_del_procedimiento = 'Publicado' AND modalidad_de_contratacion = 'Licitación pública' AND tipo_de_contrato = 'Obra' AND %s >= '%sT00:00:00.000'",
                fechaField,
                oneYearAgoFormatted
        );

        String uriQuery = UriComponentsBuilder.newInstance()
                .queryParam("$select", selectFields.toString())
                .queryParam("$where", whereClause)
                .queryParam("$order", fechaField + " DESC")
                .queryParam("$limit", pageSize)
                .queryParam("$offset", offset)
                .build()
                .getQuery();

        System.out.println("➡️ Llamando a SECOP con URI (v2 - GET): " + baseUrl + "?" + uriQuery);

        try {
            List<SecopLicitacionDTO> dtos = restClient.get()
                    .uri("?" + uriQuery)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});

            if (dtos == null) {
                System.out.println("✅ SECOP no respondió con registros.");
                return Collections.emptyList();
            }

            System.out.println("✅ SECOP respondió con " + dtos.size() + " registros");

            return dtos.stream()
                    .map(mapper::toEntity)
                    .toList();

        } catch (Exception e) {
            System.err.println("Error al llamar a la API de SECOP: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
