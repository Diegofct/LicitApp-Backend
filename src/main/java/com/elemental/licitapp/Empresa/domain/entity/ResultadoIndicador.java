package com.elemental.licitapp.Empresa.domain.entity;

import com.elemental.licitapp.Empresa.domain.enums.EstadoIndicador;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Value object del resultado de un ratio financiero: el valor calculado más su
 * estado. Encapsula la única lógica correcta de "qué pasa cuando se divide".
 *
 * <p>Reemplaza al antiguo {@code ratio()} que devolvía 0 ante una división por
 * cero, lo que inducía falsos cumplimientos/incumplimientos en el análisis. Aquí
 * un indeterminado tiene {@code valor == null} y un {@link EstadoIndicador} que
 * explica el porqué; nunca un 0 que miente.</p>
 *
 * <p>Es dominio puro: sin Spring ni JPA. La entidad {@code IndicadoresFinancieros}
 * lo usa y desempaqueta en sus columnas (valor + estado).</p>
 */
public record ResultadoIndicador(BigDecimal valor, EstadoIndicador estado) {

    /**
     * Calcula {@code numerador / denominador} con la escala y redondeo dados,
     * resolviendo los casos borde con estado explícito:
     * <ul>
     *   <li>Numerador o denominador nulo → {@link EstadoIndicador#DATO_FALTANTE}, valor null.</li>
     *   <li>Denominador cero → {@code estadoSiDenominadorCero}, valor null. El llamador
     *       decide si ese cero es favorable (cumple) o a verificar, porque depende del
     *       indicador (liquidez/RCI sin denominador = favorable; endeudamiento/ROE/ROA = a verificar).</li>
     *   <li>En otro caso → {@link EstadoIndicador#CALCULABLE} con el valor numérico.</li>
     * </ul>
     */
    public static ResultadoIndicador calcular(BigDecimal numerador, BigDecimal denominador,
                                              int escala, EstadoIndicador estadoSiDenominadorCero) {
        if (numerador == null || denominador == null) {
            return new ResultadoIndicador(null, EstadoIndicador.DATO_FALTANTE);
        }
        if (denominador.compareTo(BigDecimal.ZERO) == 0) {
            return new ResultadoIndicador(null, estadoSiDenominadorCero);
        }
        BigDecimal valor = numerador.divide(denominador, escala, RoundingMode.HALF_UP);
        return new ResultadoIndicador(valor, EstadoIndicador.CALCULABLE);
    }
}
