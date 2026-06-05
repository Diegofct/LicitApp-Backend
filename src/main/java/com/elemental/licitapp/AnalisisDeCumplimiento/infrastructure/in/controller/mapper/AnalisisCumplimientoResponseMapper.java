package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.mapper;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.DetalleRequisito;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.IntegrantePropuesto;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.PropuestaConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.DetalleRequisitoDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.EvaluarCumplimientoResponseDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.IntegrantePropuestoDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.PropuestaConsorcioDTO;

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

    private static List<PropuestaConsorcioDTO> toSugerenciasDTO(List<PropuestaConsorcio> sugerencias) {
        if (sugerencias == null) return List.of();
        return sugerencias.stream()
            .map(p -> new PropuestaConsorcioDTO(
                toIntegrantesDTO(p.integrantes()),
                p.cumpleGlobal(),
                p.requisitosCubiertos(),
                p.requisitosPendientes()))
            .toList();
    }

    private static List<IntegrantePropuestoDTO> toIntegrantesDTO(List<IntegrantePropuesto> integrantes) {
        if (integrantes == null) return List.of();
        return integrantes.stream()
            .map(i -> new IntegrantePropuestoDTO(
                i.empresaId(), i.nit(), i.razonSocial(), i.porcentaje(), i.solicitante(), i.requisitosQueCubre()))
            .toList();
    }
}
