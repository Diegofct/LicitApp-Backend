package com.elemental.licitapp.Seguridad.infrastructure.in.controller;

import com.elemental.licitapp.Exception.ResourceNotFoundException;
import com.elemental.licitapp.Seguridad.application.ports.in.AutenticarUsuarioUseCase;
import com.elemental.licitapp.Seguridad.application.ports.in.AutenticarUsuarioUseCase.ResultadoAutenticacion;
import com.elemental.licitapp.Seguridad.application.ports.in.RegistrarUsuarioUseCase;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.LoginRequestDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.TokenResponseDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.UsuarioResponseDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    public AuthController(AutenticarUsuarioUseCase autenticarUsuarioUseCase,
                          RegistrarUsuarioUseCase registrarUsuarioUseCase) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        ResultadoAutenticacion resultado =
                autenticarUsuarioUseCase.autenticar(request.getCorreo(), request.getContrasena());
        return ResponseEntity.ok(UsuarioMapper.toResponse(resultado));
    }

    /** Datos del usuario autenticado a partir del token (para rehidratar la sesion en Angular). */
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> usuarioActual(Authentication authentication) {
        String correo = authentication.getName();
        return registrarUsuarioUseCase.obtenerPorCorreo(correo)
                .map(UsuarioMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + correo));
    }
}
