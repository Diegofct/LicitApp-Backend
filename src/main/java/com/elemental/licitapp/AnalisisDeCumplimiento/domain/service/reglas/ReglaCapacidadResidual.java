package com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReglaCapacidadResidual {

    /**
     * Calcula la Capacidad Residual del Proponente (CRP).
     * CRP = CO * [(E + CT + CF) / 100] - SCE
     * 
     * @param co Capacidad de Organización
     * @param e Experiencia (Score)
     * @param ct Capacidad Técnica (Score)
     * @param cf Capacidad Financiera (Score)
     * @param sce Saldos de Contratos en Ejecución
     */
    public static BigDecimal calcularCRP(BigDecimal co, BigDecimal e, BigDecimal ct, BigDecimal cf, BigDecimal sce) {
        if (co == null) co = BigDecimal.ZERO;
        if (e == null) e = BigDecimal.ZERO;
        if (ct == null) ct = BigDecimal.ZERO;
        if (cf == null) cf = BigDecimal.ZERO;
        if (sce == null) sce = BigDecimal.ZERO;

        BigDecimal sumaPuntajes = e.add(ct).add(cf);
        BigDecimal factor = sumaPuntajes.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);
        return co.multiply(factor).subtract(sce);
    }

    /**
     * Calcula el Capital de Trabajo Demandado (CTd) para plazos < 12 meses.
     * CTd = (Presupuesto - Anticipo) * 33%
     */
    public static BigDecimal calcularCTd(BigDecimal presupuesto, BigDecimal anticipo) {
        if (presupuesto == null) presupuesto = BigDecimal.ZERO;
        if (anticipo == null) anticipo = BigDecimal.ZERO;
        return presupuesto.subtract(anticipo).multiply(new BigDecimal("0.33")).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Calcula la Capacidad Residual del Proceso (CRPC) para plazos <= 12 meses.
     * CRPC = Presupuesto - Anticipo
     */
    public static BigDecimal calcularCRPC(BigDecimal presupuesto, BigDecimal anticipo) {
        if (presupuesto == null) presupuesto = BigDecimal.ZERO;
        if (anticipo == null) anticipo = BigDecimal.ZERO;
        return presupuesto.subtract(anticipo);
    }
}
