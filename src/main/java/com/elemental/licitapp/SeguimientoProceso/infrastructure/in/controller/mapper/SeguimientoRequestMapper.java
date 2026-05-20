package com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.mapper;

import com.elemental.licitapp.SeguimientoProceso.domain.entity.EventoSeguimiento;
import com.elemental.licitapp.SeguimientoProceso.domain.entity.SeguimientoProceso;
import com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto.EventoSeguimientoResponseDTO;
import com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto.RegistrarEventoRequestDTO;
import com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto.SeguimientoResponseDTO;

import java.util.List;

public final class SeguimientoRequestMapper {

    private SeguimientoRequestMapper() {}

    public static EventoSeguimiento toEntity(RegistrarEventoRequestDTO dto) {
        if (dto == null) return null;
        return EventoSeguimiento.builder()
                .tipo(dto.getTipo())
                .fechaEvento(dto.getFechaEvento())
                .descripcion(dto.getDescripcion())
                .build();
    }

    public static SeguimientoResponseDTO toResponse(SeguimientoProceso s) {
        if (s == null) return null;
        return SeguimientoResponseDTO.builder()
                .id(s.getId())
                .cuadroDeObraId(s.getCuadroDeObraId())
                .fechaInicio(s.getFechaInicio())
                .eventos(s.getEventos() == null ? List.of()
                        : s.getEventos().stream()
                                .map(SeguimientoRequestMapper::toResponse)
                                .toList())
                .build();
    }

    public static EventoSeguimientoResponseDTO toResponse(EventoSeguimiento e) {
        if (e == null) return null;
        return EventoSeguimientoResponseDTO.builder()
                .id(e.getId())
                .tipo(e.getTipo())
                .fechaEvento(e.getFechaEvento())
                .descripcion(e.getDescripcion())
                .fechaRegistro(e.getFechaRegistro())
                .build();
    }
}
