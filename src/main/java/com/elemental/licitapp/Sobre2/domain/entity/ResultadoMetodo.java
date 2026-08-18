package com.elemental.licitapp.Sobre2.domain.entity;

import com.elemental.licitapp.Sobre2.domain.enums.MetodoPonderacion;

import java.math.BigDecimal;

/**
 * Resultado del calculo de un metodo de ponderacion sobre la muestra de oferentes.
 *
 * @param valorReferencia    valor central que arroja la formula
 * @param valorObjetivo      valor que hoy obtendria el maximo puntaje. Difiere de la
 *                           referencia en la mediana con numero par de ofertas (donde el
 *                           maximo va a la propuesta inmediatamente por debajo) y en el
 *                           menor valor
 * @param puntajeCandidato   puntaje que sacaria el valor candidato, ya incluido en la
 *                           muestra; null si no se simulo ninguno
 * @param posicionCandidato  puesto del candidato por puntaje (1 = mejor); null si no se simulo
 * @param puntajeSugerido    puntaje que sacaria el valor sugerido en este metodo. Es la
 *                           pregunta que se hace al decidir: si la TRM sortea este metodo y
 *                           me presente con el sugerido, cuanto saco
 */
public record ResultadoMetodo(
        MetodoPonderacion metodo,
        BigDecimal valorReferencia,
        BigDecimal porcentajeReferencia,
        BigDecimal valorObjetivo,
        String oferenteMasCercano,
        BigDecimal puntajeCandidato,
        Integer posicionCandidato,
        BigDecimal puntajeSugerido
) {
}
