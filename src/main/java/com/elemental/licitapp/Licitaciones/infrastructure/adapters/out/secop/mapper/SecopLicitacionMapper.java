package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.mapper;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopLicitacionDTO;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

@Component
public class SecopLicitacionMapper {

    public Licitacion toEntity(SecopLicitacionDTO dto) {
        if (dto == null) {
            return null;
        }

        Licitacion licitacion = new Licitacion();
        licitacion.setIdDelProceso(dto.getIdDelProceso());
        licitacion.setEntidad(dto.getEntidad());
        licitacion.setObjeto(dto.getObjeto());
        licitacion.setCuantia(dto.getCuantia());
        licitacion.setModalidad(dto.getModalidad());
        licitacion.setNumero(dto.getNumero());
        licitacion.setEstado(dto.getEstado());
        licitacion.setCodigoUnpspc(dto.getCodigoUnpspc());
        licitacion.setFechaPublicacion(dto.getFechaPublicacionConsolidada());
        licitacion.setUbicacion(buildUbicacion(dto));
        licitacion.setUrlSecop(extractUrl(dto.getUrlProceso()));
        return licitacion;
    }

    private String buildUbicacion(SecopLicitacionDTO dto) {
        String depto = dto.getDepartamentoEntidad() != null ? dto.getDepartamentoEntidad() : "";
        String ciudad = dto.getCiudadEntidad() != null ? dto.getCiudadEntidad() : "";
        return depto + " - " + ciudad;
    }

    // SECOP entrega urlproceso a veces como objeto {url, description} y otras como string plano.
    private String extractUrl(JsonNode urlProceso) {
        if (urlProceso == null || urlProceso.isNull()) {
            return null;
        }
        if (urlProceso.isTextual()) {
            return urlProceso.asText();
        }
        JsonNode urlField = urlProceso.get("url");
        return urlField != null && !urlField.isNull() ? urlField.asText() : null;
    }
}
