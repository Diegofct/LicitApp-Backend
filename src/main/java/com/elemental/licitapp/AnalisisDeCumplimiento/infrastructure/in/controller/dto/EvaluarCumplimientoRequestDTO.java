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
public class EvaluarCumplimientoRequestDTO {

    @NotNull(message = "empresaId es obligatorio")
    @Positive(message = "empresaId debe ser positivo")
    private Long empresaId;

    @NotNull(message = "cuadroDeObraId es obligatorio")
    @Positive(message = "cuadroDeObraId debe ser positivo")
    private Long cuadroDeObraId;

    /**
     * Fracción en (0,1) que se asume para la empresaId al simular consorcios
     * con otras empresas (sugerencia de socios). La empresa candidata recibe
     * el complemento. Opcional; si se omite se usa 0.5.
     */
    @DecimalMin(value = "0.01", inclusive = true, message = "porcentajeSimulacion debe ser >= 0.01")
    @DecimalMax(value = "0.99", inclusive = true, message = "porcentajeSimulacion debe ser <= 0.99")
    private BigDecimal porcentajeSimulacion;
}
