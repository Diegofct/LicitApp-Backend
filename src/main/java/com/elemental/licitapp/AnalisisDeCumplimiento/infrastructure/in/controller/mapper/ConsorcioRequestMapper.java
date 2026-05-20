package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.mapper;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.IntegranteConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.ConsorcioResponseDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.GuardarConsorcioRequestDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.IntegranteConsorcioRequestDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.IntegranteConsorcioResponseDTO;

import java.util.List;
import java.util.stream.Collectors;

public final class ConsorcioRequestMapper {

    private ConsorcioRequestMapper() {}

    public static ConformacionConsorcio toEntity(GuardarConsorcioRequestDTO dto) {
        if (dto == null) return null;

        ConformacionConsorcio c = ConformacionConsorcio.builder()
                .cuadroDeObraId(dto.getCuadroDeObraId())
                .tipoParticipacion(dto.getTipoParticipacion())
                .observaciones(dto.getObservaciones())
                .build();

        if (dto.getIntegrantes() != null) {
            List<IntegranteConsorcio> integrantes = dto.getIntegrantes().stream()
                    .map(ConsorcioRequestMapper::toEntity)
                    .collect(Collectors.toList());
            c.getIntegrantes().addAll(integrantes);
        }
        return c;
    }

    public static IntegranteConsorcio toEntity(IntegranteConsorcioRequestDTO dto) {
        if (dto == null) return null;
        return IntegranteConsorcio.builder()
                .empresaId(dto.getEmpresaId())
                .porcentajeParticipacion(dto.getPorcentajeParticipacion())
                .build();
    }

    public static ConsorcioResponseDTO toResponse(ConformacionConsorcio c) {
        if (c == null) return null;
        return ConsorcioResponseDTO.builder()
                .id(c.getId())
                .cuadroDeObraId(c.getCuadroDeObraId())
                .tipoParticipacion(c.getTipoParticipacion())
                .fechaConformacion(c.getFechaConformacion())
                .observaciones(c.getObservaciones())
                .integrantes(c.getIntegrantes() == null ? List.of()
                        : c.getIntegrantes().stream()
                                .map(ConsorcioRequestMapper::toResponse)
                                .toList())
                .build();
    }

    public static IntegranteConsorcioResponseDTO toResponse(IntegranteConsorcio i) {
        if (i == null) return null;
        return IntegranteConsorcioResponseDTO.builder()
                .id(i.getId())
                .empresaId(i.getEmpresaId())
                .porcentajeParticipacion(i.getPorcentajeParticipacion())
                .build();
    }
}
