package com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Reglas de capacidad residual aplicadas al PROCESO de contratación.
 * Los cálculos relativos al proponente (CRP) viven en la entidad
 * {@code CapacidadResidualProponente} del módulo Empresa.
 */
public class ReglaCapacidadResidual {

    private static final BigDecimal FACTOR_CTD_MENOR_12 = new BigDecimal("0.33");
    private static final int ESCALA_MONEDA = 2;

    /**
     * Capital de Trabajo Demandado (CTd) para plazos < 12 meses.
     * CTd = (Presupuesto - Anticipo) * 33%
     */
    public static BigDecimal calcularCTd(BigDecimal presupuesto, BigDecimal anticipo) {
        return safe(presupuesto).subtract(safe(anticipo))
                .multiply(FACTOR_CTD_MENOR_12)
                .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    /**
     * Capacidad Residual del Proceso (CRPC) para plazos <= 12 meses.
     * CRPC = Presupuesto - Anticipo
     */
    public static BigDecimal calcularCRPC(BigDecimal presupuesto, BigDecimal anticipo) {
        return safe(presupuesto).subtract(safe(anticipo))
                .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    private static BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
