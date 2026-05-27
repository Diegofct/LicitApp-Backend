package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;

/**
 * Evalúa el cumplimiento de un cuadro de obra usando la ConformacionConsorcio
 * ya registrada para ese cuadro (con los porcentajes reales pactados entre
 * los integrantes). Si la conformación es INDIVIDUAL, delega al flujo
 * individual. Si es CONSORCIO o UNION_TEMPORAL, aplica ponderación por los
 * porcentajes reales de cada integrante.
 */
public interface EvaluarConformacionUseCase {
    ResultadoEvaluacion evaluarConformacion(Long cuadroDeObraId);
}
