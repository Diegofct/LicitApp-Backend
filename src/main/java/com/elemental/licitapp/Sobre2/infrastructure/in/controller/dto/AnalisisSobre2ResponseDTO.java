package com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto;

import java.math.BigDecimal;
import java.util.List;

public record AnalisisSobre2ResponseDTO(
        Long cuadroDeObraId,
        String numeroProceso,
        BigDecimal presupuestoOficial,
        String regimen,
        int totalOferentes,
        int oferentesValidos,
        BigDecimal puntajeMaximo,
        BigDecimal valorCandidato,
        BigDecimal porcentajeCandidato,
        BigDecimal valorSugerido,
        BigDecimal porcentajeSugerido,
        List<ResultadoMetodoResponseDTO> metodos,
        List<String> advertencias,
        // Momento del proceso. Campos anadidos al final para no romper el contrato que el
        // frontend ya consume: reemplazan las heuristicas con las que hoy infiere si los
        // valores de los competidores son definitivos.
        String estadoCuadro,
        boolean listoParaDecidir,
        boolean valoresDefinitivos,
        // A que tendencia le apunta valorSugerido. Con esto el frontend puede armar la frase
        // completa: "2.502.409.860 = 93,47% - Mediana con Valor Absoluto".
        String metodoSugerido,
        String nombreMetodoSugerido
) {
}
