package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import jakarta.validation.constraints.NotNull;
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
public class CapacidadResidualProponenteRequestDTO {

    private Long id;

    @NotNull(message = "capacidadOrganizacion es obligatorio")
    @PositiveOrZero(message = "capacidadOrganizacion no puede ser negativo")
    private BigDecimal capacidadOrganizacion;

    @NotNull(message = "experiencia es obligatorio")
    @PositiveOrZero(message = "experiencia no puede ser negativa")
    private BigDecimal experiencia;

    @NotNull(message = "capacidadTecnica es obligatorio")
    @PositiveOrZero(message = "capacidadTecnica no puede ser negativa")
    private BigDecimal capacidadTecnica;

    @NotNull(message = "capacidadFinanciera es obligatorio")
    @PositiveOrZero(message = "capacidadFinanciera no puede ser negativa")
    private BigDecimal capacidadFinanciera;

    @NotNull(message = "saldosContratosEjecucion es obligatorio")
    @PositiveOrZero(message = "saldosContratosEjecucion no puede ser negativo")
    private BigDecimal saldosContratosEjecucion;
}
