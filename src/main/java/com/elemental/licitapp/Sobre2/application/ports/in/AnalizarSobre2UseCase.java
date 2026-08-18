package com.elemental.licitapp.Sobre2.application.ports.in;

import com.elemental.licitapp.Sobre2.domain.entity.AnalisisSobre2;
import com.elemental.licitapp.Sobre2.domain.enums.RegimenPonderacion;

import java.math.BigDecimal;

public interface AnalizarSobre2UseCase {

    /**
     * Calcula los metodos de ponderacion del regimen sobre los oferentes ya cargados.
     *
     * @param valorCandidato valor que el licitador piensa ofertar. Si viene, se incluye en
     *                       la muestra antes de recalcular las referencias: es la unica
     *                       forma de ver el efecto real de la propia oferta
     * @param puntajeMaximo  puntaje que el pliego asigna al factor economico
     */
    AnalisisSobre2 analizar(Long cuadroId,
                            RegimenPonderacion regimen,
                            BigDecimal valorCandidato,
                            BigDecimal puntajeMaximo);
}
