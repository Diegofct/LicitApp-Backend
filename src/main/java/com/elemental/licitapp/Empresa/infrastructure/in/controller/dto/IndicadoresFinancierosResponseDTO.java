package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import com.elemental.licitapp.Empresa.domain.enums.EstadoIndicador;
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

    // Ratios: el valor es null cuando el indicador es indeterminado; el estado explica el porqué.
    private BigDecimal liquidez;
    private EstadoIndicador estadoLiquidez;

    private BigDecimal endeudamiento;
    private EstadoIndicador estadoEndeudamiento;

    private BigDecimal razonCoberturaInteres;
    private EstadoIndicador estadoRazonCoberturaInteres;

    private BigDecimal patrimonio;
    private BigDecimal capitalTrabajo;

    private BigDecimal rentabilidadPatrimonio;
    private EstadoIndicador estadoRentabilidadPatrimonio;

    private BigDecimal rentabilidadActivo;
    private EstadoIndicador estadoRentabilidadActivo;
}
