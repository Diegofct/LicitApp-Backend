package com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto;

import java.math.BigDecimal;

/**
 * @param rangoTrm         centavos de la TRM que hacen que la entidad aplique este metodo
 * @param valorReferencia  valor central que arroja la formula
 * @param valorObjetivo    valor que hoy obtendria el maximo puntaje (difiere de la
 *                         referencia en la mediana con numero par de ofertas y en menor valor)
 * @param puntajeSugerido  puntaje que sacaria el valor sugerido si la TRM sortea este metodo
 */
public record ResultadoMetodoResponseDTO(
        String metodo,
        String nombre,
        String regimen,
        String rangoTrm,
        BigDecimal valorReferencia,
        BigDecimal porcentajeReferencia,
        BigDecimal valorObjetivo,
        String oferenteMasCercano,
        BigDecimal puntajeCandidato,
        Integer posicionCandidato,
        BigDecimal puntajeSugerido
) {
}
