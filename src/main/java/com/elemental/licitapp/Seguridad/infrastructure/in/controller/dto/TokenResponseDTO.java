package com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenResponseDTO {

    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private long expiraEnSegundos;
    private Long usuarioId;
    private String nombre;
    private String correo;
    private String rol;
}
