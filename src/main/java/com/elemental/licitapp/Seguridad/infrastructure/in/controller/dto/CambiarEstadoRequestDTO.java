package com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de PATCH /usuarios/{id}/estado. true = activar, false = desactivar (borrado logico). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CambiarEstadoRequestDTO {

    @NotNull(message = "activo es obligatorio")
    private Boolean activo;
}
