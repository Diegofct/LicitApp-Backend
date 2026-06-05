package com.elemental.licitapp.Seguridad.infrastructure.in.controller.mapper;

import com.elemental.licitapp.Seguridad.application.ports.in.AutenticarUsuarioUseCase.ResultadoAutenticacion;
import com.elemental.licitapp.Seguridad.domain.entity.Usuario;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.TokenResponseDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.UsuarioResponseDTO;

public final class UsuarioMapper {

    private UsuarioMapper() {}

    public static UsuarioResponseDTO toResponse(Usuario u) {
        if (u == null) return null;
        return UsuarioResponseDTO.builder()
                .id(u.getId())
                .nombre(u.getNombre())
                .correo(u.getCorreo())
                .rol(u.getRol() == null ? null : u.getRol().name())
                .activo(u.isActivo())
                .fechaCreacion(u.getFechaCreacion())
                .build();
    }

    public static TokenResponseDTO toResponse(ResultadoAutenticacion r) {
        if (r == null) return null;
        return TokenResponseDTO.builder()
                .token(r.token())
                .tipo("Bearer")
                .expiraEnSegundos(r.expiraEnSegundos())
                .usuarioId(r.usuarioId())
                .nombre(r.nombre())
                .correo(r.correo())
                .rol(r.rol())
                .build();
    }
}
