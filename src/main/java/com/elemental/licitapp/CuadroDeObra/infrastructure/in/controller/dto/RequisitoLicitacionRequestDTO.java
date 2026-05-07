package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @PositiveOrZero(message = "n no puede ser negativo")
    private Integer n;

    @PositiveOrZero(message = "presupuesto no puede ser negativo")
    private Double presupuesto;

    @PositiveOrZero(message = "patrimonio no puede ser negativo")
    private Double patrimonio;

    @PositiveOrZero(message = "capitalTrabajo no puede ser negativo")
    private Double capitalTrabajo;

    private Double liquidez;
    private Double endeudamiento;
    private Double razonCoberturaInteres;
    private Double rentabilidadPatrimonio;
    private Double rentabilidadActivo;

    @PositiveOrZero(message = "kResidualProceso no puede ser negativo")
    private Double kResidualProceso;

    @PositiveOrZero(message = "poeAnticipo no puede ser negativo")
    private Double poeAnticipo;
}
