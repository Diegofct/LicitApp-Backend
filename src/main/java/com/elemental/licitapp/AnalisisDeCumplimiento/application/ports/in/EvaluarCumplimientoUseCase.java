package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;

public interface EvaluarCumplimientoUseCase {
    ResultadoEvaluacion evaluar(Long empresaId, Long cuadroId, TipoParticipacion tipo);
}
