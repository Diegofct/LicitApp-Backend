package com.elemental.licitapp.Empresa.domain.enums;

/**
 * Estado del cálculo de un indicador financiero del RUP.
 *
 * <p>Distingue un valor numérico real de las distintas formas de "indeterminado"
 * que aparecen cuando el denominador de un ratio es cero o falta un insumo. El
 * tratamiento de negocio (cumple / no cumple / requiere verificación) lo decide
 * el módulo de análisis a partir de este estado, no del valor numérico.</p>
 */
public enum EstadoIndicador {

    /** El indicador tiene un valor numérico real, calculado normalmente. */
    CALCULABLE,

    /**
     * Indeterminado que el negocio interpreta como cumplido por definición.
     * <p>Casos: liquidez sin pasivo corriente (no debe nada a corto plazo) y
     * razón de cobertura de intereses sin gastos de interés (no paga intereses,
     * cubre cualquier obligación). En SECOP II se reporta como "N.A. — cumple".</p>
     */
    INDETERMINADO_FAVORABLE,

    /**
     * Indeterminado no evaluable: no se puede afirmar que cumple y debe ir a
     * verificación/subsanación, NO darse por cumplido ni por incumplido en silencio.
     * <p>Casos: endeudamiento/ROA sin activos totales, ROE sin patrimonio. Son
     * señales de un dato sospechoso o de inviabilidad financiera, no un ratio de 0.</p>
     */
    INDETERMINADO_A_VERIFICAR,

    /**
     * Faltó un insumo para calcular el indicador (numerador o denominador nulo).
     * Distinto de un cálculo indeterminado: el proponente no cargó el dato. Subsanable.
     */
    DATO_FALTANTE
}
