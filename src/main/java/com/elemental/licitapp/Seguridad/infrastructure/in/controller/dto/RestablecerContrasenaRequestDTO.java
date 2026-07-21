package com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de PATCH /usuarios/{id}/contrasena. La nueva contrasena la asigna un ADMIN. */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestablecerContrasenaRequestDTO {

    @NotBlank(message = "contrasena es obligatoria")
    @Size(min = 8, message = "contrasena debe tener al menos 8 caracteres")
    private String contrasena;
}
