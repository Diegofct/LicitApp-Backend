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
public class CapacidadResidualProponenteResponseDTO {

    private Long id;
    private BigDecimal capacidadOrganizacion;
    private BigDecimal experiencia;
    private BigDecimal capacidadTecnica;
    private BigDecimal capacidadFinanciera;
    private BigDecimal saldosContratosEjecucion;
    private BigDecimal resultadoCapacidadResidualProponente;
}
