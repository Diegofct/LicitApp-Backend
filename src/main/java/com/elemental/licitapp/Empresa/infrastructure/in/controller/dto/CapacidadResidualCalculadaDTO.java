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
public class CapacidadResidualCalculadaDTO {
    private BigDecimal resultadoCapacidadResidualProponente;
}
