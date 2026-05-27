package com.elemental.licitapp.Resultados.infrastructure.in.controller.mapper;

import com.elemental.licitapp.Resultados.domain.entity.ItemHistorialResultado;
import com.elemental.licitapp.Resultados.domain.entity.ResumenResultados;
import com.elemental.licitapp.Resultados.infrastructure.in.controller.dto.ItemHistorialResultadoResponseDTO;
import com.elemental.licitapp.Resultados.infrastructure.in.controller.dto.ResumenResultadosResponseDTO;

public final class ResultadosResponseMapper {

    private ResultadosResponseMapper() {
    }

    public static ResumenResultadosResponseDTO toResponse(ResumenResultados resumen) {
        return new ResumenResultadosResponseDTO(
                resumen.totalProcesos(),
                resumen.porPresentar(),
                resumen.presentados(),
                resumen.adjudicados(),
                resumen.noAdjudicados(),
                resumen.cancelados(),
                resumen.procesosCerrados(),
                resumen.tasaExitoPorcentaje()
        );
    }

    public static ItemHistorialResultadoResponseDTO toResponse(ItemHistorialResultado item) {
        return new ItemHistorialResultadoResponseDTO(
                item.id(),
                item.numeroProceso(),
                item.entidadContratante(),
                item.descripcionObjeto(),
                item.monto(),
                item.estado(),
                item.observacion(),
                item.fechaPublicacion(),
                item.fechaCierre()
        );
    }
}
