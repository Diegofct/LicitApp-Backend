package com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ReglaFinanciera {

    public static BigDecimal calcularLiquidez(BigDecimal activoCorriente, BigDecimal pasivoCorriente) {
        if (pasivoCorriente == null || pasivoCorriente.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return activoCorriente.divide(pasivoCorriente, 4, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularEndeudamiento(BigDecimal pasivoTotal, BigDecimal activoTotal) {
        if (activoTotal == null || activoTotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return pasivoTotal.divide(activoTotal, 4, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularRCI(BigDecimal utilidadOperacional, BigDecimal gastosInteres) {
        if (gastosInteres == null || gastosInteres.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return utilidadOperacional.divide(gastosInteres, 4, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularROE(BigDecimal utilidadOperacional, BigDecimal patrimonio) {
        if (patrimonio == null || patrimonio.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return utilidadOperacional.divide(patrimonio, 4, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularROA(BigDecimal utilidadOperacional, BigDecimal activoTotal) {
        if (activoTotal == null || activoTotal.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
        return utilidadOperacional.divide(activoTotal, 4, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularCapitalTrabajo(BigDecimal activoCorriente, BigDecimal pasivoCorriente) {
        if (activoCorriente == null) activoCorriente = BigDecimal.ZERO;
        if (pasivoCorriente == null) pasivoCorriente = BigDecimal.ZERO;
        return activoCorriente.subtract(pasivoCorriente);
    }
}
