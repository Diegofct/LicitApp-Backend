package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.mapper;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopLicitacionDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class SecopLicitacionMapper {

    private final ModelMapper modelMapper;

    public SecopLicitacionMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

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

        String ubicacion = (dto.getDepartamentoEntidad() != null ? dto.getDepartamentoEntidad() : "") +
                " - " +
                (dto.getCiudadEntidad() != null ? dto.getCiudadEntidad() : "");
        licitacion.setUbicacion(ubicacion);

        // Custom mapping for urlSecop
        if (dto.getUrlProceso() != null && dto.getUrlProceso().isContainerNode()) {
            licitacion.setUrlSecop(dto.getUrlProceso().get("url").asText());
        }

        return licitacion;
    }

    public SecopLicitacionDTO toDTO(Licitacion entity) {
        return modelMapper.map(entity, SecopLicitacionDTO.class);
    }
}
