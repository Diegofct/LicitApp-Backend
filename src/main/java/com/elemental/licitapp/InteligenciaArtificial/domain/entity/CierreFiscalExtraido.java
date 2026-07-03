package com.elemental.licitapp.InteligenciaArtificial.domain.entity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.math.BigDecimal;

/**
 * Un cierre fiscal reportado en el RUP. Se extraen TODOS los cierres que figuren;
 * el analista elige cual guardar en el frontend.
 *
 * <p>Solo los 6 valores absolutos son fuente de verdad: los indicadores derivados se
 * recalculan con {@code IndicadoresFinancieros.recalcular()}. Los indicadores
 * <b>impresos</b> en el RUP se extraen aparte unicamente para verificacion cruzada
 * (si difieren mas de 1% de los recalculados, se genera una advertencia).
 */
public record CierreFiscalExtraido(

        @JsonPropertyDescription("Ano del cierre fiscal reportado, ej. 2024")
        Integer anioCierre,

        @JsonPropertyDescription("Activo corriente (valor absoluto en pesos)")
        BigDecimal activoCorriente,

        @JsonPropertyDescription("Pasivo corriente (valor absoluto en pesos)")
        BigDecimal pasivoCorriente,

        @JsonPropertyDescription("Activo total (valor absoluto en pesos)")
        BigDecimal activoTotal,

        @JsonPropertyDescription("Pasivo total (valor absoluto en pesos)")
        BigDecimal pasivoTotal,

        @JsonPropertyDescription("Utilidad operacional (valor absoluto en pesos)")
        BigDecimal utilidadOperacional,

        @JsonPropertyDescription("Gastos de interes (valor absoluto en pesos)")
        BigDecimal gastosInteres,

        @JsonPropertyDescription("Indice de liquidez IMPRESO en el RUP (solo para verificacion, no se guarda)")
        BigDecimal liquidezImpresa,

        @JsonPropertyDescription("Nivel de endeudamiento IMPRESO en el RUP (solo para verificacion, no se guarda)")
        BigDecimal endeudamientoImpreso,

        @JsonPropertyDescription("Razon de cobertura de intereses IMPRESA en el RUP (solo para verificacion, no se guarda)")
        BigDecimal razonCoberturaInteresImpresa,

        @JsonPropertyDescription("Rentabilidad del patrimonio IMPRESA en el RUP (solo para verificacion, no se guarda)")
        BigDecimal rentabilidadPatrimonioImpresa,

        @JsonPropertyDescription("Rentabilidad del activo IMPRESA en el RUP (solo para verificacion, no se guarda)")
        BigDecimal rentabilidadActivoImpresa
) {
}
