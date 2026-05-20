package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegranteConsorcioRequestDTO {

    @NotNull(message = "empresaId es obligatorio")
    @Positive(message = "empresaId debe ser positivo")
    private Long empresaId;

    @NotNull(message = "porcentajeParticipacion es obligatorio")
    @DecimalMin(value = "0.01", message = "porcentajeParticipacion debe ser mayor a 0")
    @DecimalMax(value = "100.00", message = "porcentajeParticipacion no puede ser mayor a 100")
    private BigDecimal porcentajeParticipacion;
}
