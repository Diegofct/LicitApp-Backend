package com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestDTO {

    @NotBlank(message = "correo es obligatorio")
    @Email(message = "correo no tiene formato valido")
    private String correo;

    @NotBlank(message = "contrasena es obligatoria")
    private String contrasena;
}
