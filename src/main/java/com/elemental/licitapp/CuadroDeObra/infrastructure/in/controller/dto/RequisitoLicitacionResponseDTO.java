package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private Double presupuesto;
    private Double patrimonio;
    private Double capitalTrabajo;
    private Double liquidez;
    private Double endeudamiento;
    private Double razonCoberturaInteres;
    private Double rentabilidadPatrimonio;
    private Double rentabilidadActivo;

    private Double kResidualProceso;
    private Double poeAnticipo;
}
