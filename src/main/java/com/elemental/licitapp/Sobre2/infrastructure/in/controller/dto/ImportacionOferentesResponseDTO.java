package com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto;

import java.util.List;

/**
 * @param encontrados ofertas distintas halladas en SECOP, ya deduplicadas (SECOP publica
 *                    una fila por integrante del consorcio)
 * @param proponentesRegistrados cuantos radicaron oferta segun SECOP, cuando no llegaron
 *                    valores. Con {@code encontrados = 0} y este campo en N > 0, el proceso
 *                    tiene competencia pero el Sobre 2 aun no se ha abierto. Null cuando no
 *                    aplica o SECOP no publica el dato
 */
public record ImportacionOferentesResponseDTO(
        Long cuadroDeObraId,
        int encontrados,
        int creados,
        int actualizados,
        List<OferenteProcesoResponseDTO> oferentes,
        List<String> advertencias,
        Integer proponentesRegistrados
) {
}
