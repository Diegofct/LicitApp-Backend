package com.elemental.licitapp.Resultados.domain.entity;

import java.math.BigDecimal;

public record ResumenResultados(
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
