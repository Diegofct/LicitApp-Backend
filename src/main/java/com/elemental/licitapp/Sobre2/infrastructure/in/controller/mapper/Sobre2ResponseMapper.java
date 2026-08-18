package com.elemental.licitapp.Sobre2.infrastructure.in.controller.mapper;

import com.elemental.licitapp.Sobre2.application.ports.in.ImportarOferentesUseCase.ResultadoImportacion;
import com.elemental.licitapp.Sobre2.domain.entity.AnalisisSobre2;
import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;
import com.elemental.licitapp.Sobre2.domain.entity.ResultadoMetodo;
import com.elemental.licitapp.Sobre2.domain.entity.ResumenCompetidor;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.AnalisisSobre2ResponseDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.ImportacionOferentesResponseDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.OferenteProcesoResponseDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.ResultadoMetodoResponseDTO;
import com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto.ResumenCompetidorResponseDTO;

import java.util.List;

/** Clase utilitaria con metodos estaticos, como el resto de mappers del proyecto. */
public final class Sobre2ResponseMapper {

    private Sobre2ResponseMapper() {
    }

    public static OferenteProcesoResponseDTO toResponse(OferenteProceso o) {
        return new OferenteProcesoResponseDTO(
                o.getId(),
                o.getCuadroDeObraId(),
                o.getNombreOferente(),
                o.getNitOferente(),
                o.getValorOferta(),
                o.getPorcentaje(),
                o.getMoneda(),
                o.getFechaRegistro(),
                o.getValida(),
                o.getOrigen());
    }

    public static List<OferenteProcesoResponseDTO> toResponse(List<OferenteProceso> oferentes) {
        return oferentes.stream().map(Sobre2ResponseMapper::toResponse).toList();
    }

    public static ImportacionOferentesResponseDTO toResponse(ResultadoImportacion resultado) {
        return new ImportacionOferentesResponseDTO(
                resultado.cuadroDeObraId(),
                resultado.encontrados(),
                resultado.creados(),
                resultado.actualizados(),
                toResponse(resultado.oferentes()),
                resultado.advertencias(),
                resultado.proponentesRegistrados());
    }

    public static AnalisisSobre2ResponseDTO toResponse(AnalisisSobre2 analisis) {
        return new AnalisisSobre2ResponseDTO(
                analisis.cuadroDeObraId(),
                analisis.numeroProceso(),
                analisis.presupuestoOficial(),
                analisis.regimen().name(),
                analisis.totalOferentes(),
                analisis.oferentesValidos(),
                analisis.puntajeMaximo(),
                analisis.valorCandidato(),
                analisis.porcentajeCandidato(),
                analisis.valorSugerido(),
                analisis.porcentajeSugerido(),
                analisis.metodos().stream().map(Sobre2ResponseMapper::toResponse).toList(),
                analisis.advertencias(),
                analisis.estadoCuadro(),
                analisis.listoParaDecidir(),
                analisis.valoresDefinitivos(),
                analisis.metodoSugerido() != null ? analisis.metodoSugerido().name() : null,
                analisis.metodoSugerido() != null ? analisis.metodoSugerido().getNombre() : null);
    }

    private static ResultadoMetodoResponseDTO toResponse(ResultadoMetodo r) {
        return new ResultadoMetodoResponseDTO(
                r.metodo().name(),
                r.metodo().getNombre(),
                r.metodo().getRegimen().name(),
                r.metodo().getRangoTrm(),
                r.valorReferencia(),
                r.porcentajeReferencia(),
                r.valorObjetivo(),
                r.oferenteMasCercano(),
                r.puntajeCandidato(),
                r.posicionCandidato(),
                r.puntajeSugerido());
    }

    public static ResumenCompetidorResponseDTO toResponse(ResumenCompetidor c) {
        return new ResumenCompetidorResponseDTO(
                c.nombreOferente(),
                c.procesos(),
                c.porcentajePromedio(),
                c.porcentajeMinimo(),
                c.porcentajeMaximo());
    }
}
