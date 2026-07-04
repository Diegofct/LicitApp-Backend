package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequisitoLicitacionResponseDTO {

    private Long id;

    private String general;
    private String especifica1;
    private String especifica2;
    private String secundaria;
    private Integer contrato;
    private Integer n;

    private BigDecimal presupuesto;
    private BigDecimal patrimonio;
    private BigDecimal capitalTrabajo;
    private BigDecimal liquidez;
    private BigDecimal endeudamiento;
    private BigDecimal razonCoberturaInteres;
    private BigDecimal rentabilidadPatrimonio;
    private BigDecimal rentabilidadActivo;

    private BigDecimal kResidualProceso;
    private Double poeAnticipo;
}
