package com.elemental.licitapp.Resultados.infrastructure.in.controller.dto;

import java.math.BigDecimal;

public record ResumenResultadosResponseDTO(
        long totalProcesos,
        long porPresentar,
        long presentados,
        long adjudicados,
        long noAdjudicados,
        long cancelados,
        long procesosCerrados,
        BigDecimal tasaExitoPorcentaje
) {
}
