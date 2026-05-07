package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcularCapacidadProcesoResponseDTO {
    private BigDecimal capacidadResidualProceso; // CRPC
    private BigDecimal capitalTrabajoDemandado; // CTd
}
