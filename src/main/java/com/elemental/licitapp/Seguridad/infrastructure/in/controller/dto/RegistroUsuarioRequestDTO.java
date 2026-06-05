package com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto;

import com.elemental.licitapp.Seguridad.domain.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroUsuarioRequestDTO {

    @NotBlank(message = "nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "correo es obligatorio")
    @Email(message = "correo no tiene formato valido")
    private String correo;

    @NotBlank(message = "contrasena es obligatoria")
    @Size(min = 8, message = "contrasena debe tener al menos 8 caracteres")
    private String contrasena;

    @NotNull(message = "rol es obligatorio")
    private Rol rol;
}
