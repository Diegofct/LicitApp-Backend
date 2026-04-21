package com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReglaCapacidadResidual {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final BigDecimal FACTOR_CTD_MENOR_12 = new  BigDecimal("0.33");
    private static final int ESCALA_MONEDA = 2;
    private static final int ESCALA_CALCULO = 4;

    /**
     * Calcula la Capacidad Residual del Proponente (CRP).
     * Formula = CRP = CO * [(E + CT + CF) / 100] - SCE
     * 
     * @param co Capacidad de Organización
     * @param e Experiencia (Score)
     * @param ct Capacidad Técnica (Score)
     * @param cf Capacidad Financiera (Score)
     * @param sce Saldos de Contratos en Ejecución
     */
    public static BigDecimal calcularCRP(BigDecimal co, BigDecimal e, BigDecimal ct, BigDecimal cf, BigDecimal sce) {
        BigDecimal sumaPuntajes = safeGet(e).add(safeGet(ct)).add(safeGet(cf));

        // Calculamos el factor con 4 decimales para precisión intermedia
        BigDecimal factor = sumaPuntajes.divide(CIEN, ESCALA_CALCULO, RoundingMode.HALF_UP);

        // Resultado final: CO * factor - SCE
        return safeGet(co).multiply(factor)
                .subtract(safeGet(sce))
                .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    /**
     * Calcula el Capital de Trabajo Demandado (CTd) para plazos < 12 meses.
     * CTd = (Presupuesto - Anticipo) * 33%
     */
    public static BigDecimal calcularCTd(BigDecimal presupuesto, BigDecimal anticipo){
        return safeGet(presupuesto).subtract(safeGet(anticipo))
                .multiply(FACTOR_CTD_MENOR_12)
                .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la Capacidad Residual del Proceso (CRPC) para plazos <= 12 meses.
     * CRPC = Presupuesto - Anticipo
     */
    public static BigDecimal calcularCRPC(BigDecimal presupuesto, BigDecimal anticipo){
        return safeGet(presupuesto).subtract(safeGet(anticipo))
                .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    /**
     * Asegura que los valores nulos se traten como cero para evitar NullPointerException.
     */
    private static BigDecimal safeGet(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
