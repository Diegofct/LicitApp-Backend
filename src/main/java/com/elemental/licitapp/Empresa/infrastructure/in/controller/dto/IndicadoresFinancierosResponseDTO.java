package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndicadoresFinancierosResponseDTO {

    private Long id;
    private Integer anioCierre;

    private BigDecimal activoCorriente;
    private BigDecimal pasivoCorriente;
    private BigDecimal activoTotal;
    private BigDecimal pasivoTotal;
    private BigDecimal utilidadOperacional;
    private BigDecimal gastosInteres;

    private BigDecimal liquidez;
    private BigDecimal endeudamiento;
    private BigDecimal razonCoberturaInteres;
    private BigDecimal patrimonio;
    private BigDecimal capitalTrabajo;
    private BigDecimal rentabilidadPatrimonio;
    private BigDecimal rentabilidadActivo;
}
