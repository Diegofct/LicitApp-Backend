package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Solo recibe valores absolutos. Los indicadores derivados
 * (liquidez, endeudamiento, ROE, ROA, RCI, capital de trabajo, patrimonio)
 * se calculan en el dominio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicadoresFinancierosRequestDTO {

    private Integer anioCierre;

    @NotNull(message = "activoCorriente es obligatorio")
    @PositiveOrZero(message = "activoCorriente no puede ser negativo")
    private BigDecimal activoCorriente;

    @NotNull(message = "pasivoCorriente es obligatorio")
    @PositiveOrZero(message = "pasivoCorriente no puede ser negativo")
    private BigDecimal pasivoCorriente;

    @NotNull(message = "activoTotal es obligatorio")
    @PositiveOrZero(message = "activoTotal no puede ser negativo")
    private BigDecimal activoTotal;

    @NotNull(message = "pasivoTotal es obligatorio")
    @PositiveOrZero(message = "pasivoTotal no puede ser negativo")
    private BigDecimal pasivoTotal;

    @NotNull(message = "utilidadOperacional es obligatorio")
    private BigDecimal utilidadOperacional;

    @NotNull(message = "gastosInteres es obligatorio")
    @PositiveOrZero(message = "gastosInteres no puede ser negativo")
    private BigDecimal gastosInteres;
}
