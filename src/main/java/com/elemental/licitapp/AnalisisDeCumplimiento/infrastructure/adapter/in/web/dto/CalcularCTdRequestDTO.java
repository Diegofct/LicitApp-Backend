package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalcularCTdRequestDTO {

    @NotNull(message = "presupuestoOficial es obligatorio")
    @Positive(message = "presupuestoOficial debe ser positivo")
    private BigDecimal presupuestoOficial;

    @NotNull(message = "anticipo es obligatorio")
    @PositiveOrZero(message = "anticipo no puede ser negativo")
    private BigDecimal anticipo;
}
