package com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto;

import com.elemental.licitapp.Seguridad.domain.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuerpo de PUT /usuarios/{id}. La contrasena NO se edita por aqui (ver PATCH .../contrasena). */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActualizarUsuarioRequestDTO {

    @NotBlank(message = "nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "correo es obligatorio")
    @Email(message = "correo no tiene formato valido")
    private String correo;

    @NotNull(message = "rol es obligatorio")
    private Rol rol;
}
