package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

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
    private BigDecimal liquidez;
    private BigDecimal endeudamiento;
    private BigDecimal razonCoberturaInteres;
    private BigDecimal rentabilidadPatrimonio;
    private BigDecimal rentabilidadActivo;
    private BigDecimal capitalTrabajo;
}
