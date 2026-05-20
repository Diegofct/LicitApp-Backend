package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in;

import java.math.BigDecimal;

/**
 * Cálculos relativos al PROCESO de contratación (no al proponente).
 * El CRP del proponente vive en el módulo Empresa.
 */
public interface CalcularCapacidadProcesoUseCase {

    /**
     * Capacidad Residual del Proceso (CRPC) para plazos ≤ 12 meses.
     * CRPC = Presupuesto - Anticipo.
     */
    BigDecimal calcularCRPC(BigDecimal presupuestoOficial, BigDecimal anticipo);

    /**
     * Capital de Trabajo Demandado (CTd) para plazos < 12 meses.
     * CTd = (Presupuesto - Anticipo) × 33%.
     */
    BigDecimal calcularCTd(BigDecimal presupuestoOficial, BigDecimal anticipo);
}
