package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluarCumplimientoRequestDTO {

    @NotNull(message = "empresaId es obligatorio")
    @Positive(message = "empresaId debe ser positivo")
    private Long empresaId;

    @NotNull(message = "cuadroDeObraId es obligatorio")
    @Positive(message = "cuadroDeObraId debe ser positivo")
    private Long cuadroDeObraId;
}
