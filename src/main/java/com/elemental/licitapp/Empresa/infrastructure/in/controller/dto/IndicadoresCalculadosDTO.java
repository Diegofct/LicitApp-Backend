package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import com.elemental.licitapp.Empresa.domain.enums.EstadoIndicador;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndicadoresCalculadosDTO {
    // Ratios: valor null cuando es indeterminado; el estado acompaña y explica el porqué.
    private BigDecimal liquidez;
    private EstadoIndicador estadoLiquidez;

    private BigDecimal endeudamiento;
    private EstadoIndicador estadoEndeudamiento;

    private BigDecimal razonCoberturaInteres;
    private EstadoIndicador estadoRazonCoberturaInteres;

    private BigDecimal rentabilidadPatrimonio;
    private EstadoIndicador estadoRentabilidadPatrimonio;

    private BigDecimal rentabilidadActivo;
    private EstadoIndicador estadoRentabilidadActivo;

    private BigDecimal capitalTrabajo;
}
