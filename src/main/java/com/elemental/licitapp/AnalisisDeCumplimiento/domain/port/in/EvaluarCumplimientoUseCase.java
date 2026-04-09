package com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.in;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.TipoParticipacion;

public interface EvaluarCumplimientoUseCase {
    ResultadoEvaluacion evaluar(Long empresaId, Long cuadroId, TipoParticipacion tipo);
}
