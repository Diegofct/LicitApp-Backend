package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import java.util.List;

public record ResultadoEvaluacion(
    Long empresaId,
    Long cuadroId,
    TipoParticipacion tipo,
    boolean cumpleGlobal,
    List<DetalleRequisito> detalles,
    List<SugerenciaConsorcio> sugerencias
) {
    public ResultadoEvaluacion(Long empresaId, Long cuadroId, TipoParticipacion tipo, boolean cumpleGlobal, List<DetalleRequisito> detalles) {
        this(empresaId, cuadroId, tipo, cumpleGlobal, detalles, List.of());
    }
}
