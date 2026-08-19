package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitoLicitacionRequestDTO {

    private String general;
    private String especifica1;
    private String especifica2;
    private String secundaria;

    @PositiveOrZero(message = "contrato no puede ser negativo")
    private Integer contrato;

    @PositiveOrZero(message = "plazo no puede ser negativo")
    private Integer plazo;

    @PositiveOrZero(message = "presupuesto no puede ser negativo")
    private BigDecimal presupuesto;

    @PositiveOrZero(message = "patrimonio no puede ser negativo")
    private BigDecimal patrimonio;

    @PositiveOrZero(message = "capitalTrabajo no puede ser negativo")
    private BigDecimal capitalTrabajo;

    // Los cinco indicadores se guardan como fracción, no como porcentaje: un endeudamiento
    // exigido del 70% viaja como 0.70. El tope de 1 en los que son fracción por definición es
    // lo que atrapa el error de digitar "70". Liquidez y cobertura de intereses no lo llevan:
    // la primera ronda 1,2 y la segunda son veces (las matrices de pliego tipo llegan a 1,5 y
    // fuera de pliego tipo se exigen 3 o más).

    @PositiveOrZero(message = "liquidez no puede ser negativa")
    private BigDecimal liquidez;

    @PositiveOrZero(message = "endeudamiento no puede ser negativo")
    @DecimalMax(value = "1.00", message = "endeudamiento se expresa como fracción: 70% es 0.70")
    private BigDecimal endeudamiento;

    @PositiveOrZero(message = "razonCoberturaInteres no puede ser negativa")
    private BigDecimal razonCoberturaInteres;

    @PositiveOrZero(message = "rentabilidadPatrimonio no puede ser negativa")
    @DecimalMax(value = "1.00", message = "rentabilidadPatrimonio se expresa como fracción: 4% es 0.04")
    private BigDecimal rentabilidadPatrimonio;

    @PositiveOrZero(message = "rentabilidadActivo no puede ser negativa")
    @DecimalMax(value = "1.00", message = "rentabilidadActivo se expresa como fracción: 2% es 0.02")
    private BigDecimal rentabilidadActivo;

    @PositiveOrZero(message = "kResidualProceso no puede ser negativo")
    private BigDecimal kResidualProceso;

    @PositiveOrZero(message = "poeAnticipo no puede ser negativo")
    private Double poeAnticipo;
}
