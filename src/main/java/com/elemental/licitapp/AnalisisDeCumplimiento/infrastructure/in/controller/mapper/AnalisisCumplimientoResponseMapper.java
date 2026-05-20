package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.mapper;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.DetalleRequisito;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.SugerenciaConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.DetalleRequisitoDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.EvaluarCumplimientoResponseDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.SugerenciaConsorcioDTO;

import java.util.List;

public final class AnalisisCumplimientoResponseMapper {

    private AnalisisCumplimientoResponseMapper() {}

    public static EvaluarCumplimientoResponseDTO toResponse(ResultadoEvaluacion resultado) {
        return new EvaluarCumplimientoResponseDTO(
            resultado.empresaId(),
            resultado.cuadroId(),
            resultado.tipo().name(),
            resultado.cumpleGlobal(),
            toDetallesDTO(resultado.detalles()),
            toSugerenciasDTO(resultado.sugerencias())
        );
    }

    private static List<DetalleRequisitoDTO> toDetallesDTO(List<DetalleRequisito> detalles) {
        if (detalles == null) return List.of();
        return detalles.stream()
            .map(d -> new DetalleRequisitoDTO(d.nombre(), d.valorRequerido(), d.valorActual(), d.cumple(), d.mensaje()))
            .toList();
    }

    private static List<SugerenciaConsorcioDTO> toSugerenciasDTO(List<SugerenciaConsorcio> sugerencias) {
        if (sugerencias == null) return List.of();
        return sugerencias.stream()
            .map(s -> new SugerenciaConsorcioDTO(s.empresaId(), s.nit(), s.razonSocial()))
            .toList();
    }
}
