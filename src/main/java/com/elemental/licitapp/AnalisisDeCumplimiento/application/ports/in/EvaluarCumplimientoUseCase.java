package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;

import java.math.BigDecimal;

public interface EvaluarCumplimientoUseCase {
    /**
     * @param porcentajeSimulacion fracción en (0,1) que asume la empresaId en la
     *        simulación de sugerencias de consorcio. Si es null, se asume 0.5.
     *        La empresa candidata recibe el complemento (1 - porcentajeSimulacion).
     */
    ResultadoEvaluacion evaluar(Long empresaId, Long cuadroId, TipoParticipacion tipo, BigDecimal porcentajeSimulacion);
}
