package com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body de POST /licitaciones/revisiones: la identidad SECOP de la licitacion a marcar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcarRevisionRequestDTO {

    @NotBlank(message = "idDelProceso es obligatorio")
    private String idDelProceso;
}
