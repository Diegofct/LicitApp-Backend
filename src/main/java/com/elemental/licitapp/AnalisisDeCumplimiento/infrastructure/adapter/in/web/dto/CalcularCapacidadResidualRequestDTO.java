package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcularCapacidadResidualRequestDTO {
    private BigDecimal capacidadOrganizacion;
    private BigDecimal puntajeExperiencia;
    private BigDecimal puntajeTecnico;
    private BigDecimal puntajeFinanciero;
    private BigDecimal saldosContratosEjecucion;
}
