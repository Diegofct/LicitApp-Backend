package com.elemental.licitapp.Seguridad.infrastructure.in.controller;

import com.elemental.licitapp.Seguridad.application.ports.in.RegistrarUsuarioUseCase;
import com.elemental.licitapp.Seguridad.domain.entity.Usuario;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.ActualizarUsuarioRequestDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.CambiarEstadoRequestDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.RegistroUsuarioRequestDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.RestablecerContrasenaRequestDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.dto.UsuarioResponseDTO;
import com.elemental.licitapp.Seguridad.infrastructure.in.controller.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Gestion de usuarios. Todo el controlador queda restringido a ADMIN en
 * SecurityConfig (matcher /usuarios/**). No hay auto-registro publico.
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;

    public UsuarioController(RegistrarUsuarioUseCase registrarUsuarioUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody RegistroUsuarioRequestDTO request) {
        Usuario usuario = registrarUsuarioUseCase.registrar(
                request.getNombre(), request.getCorreo(), request.getContrasena(), request.getRol());
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toResponse(usuario));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        List<UsuarioResponseDTO> respuesta = registrarUsuarioUseCase.listar().stream()
                .map(UsuarioMapper::toResponse)
                .toList();
        return ResponseEntity.ok(respuesta);
    }

    /** Actualiza nombre, correo y rol. La contrasena se cambia por separado. */
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> actualizar(@PathVariable Long id,
            @Valid @RequestBody ActualizarUsuarioRequestDTO request, Authentication authentication) {
        Usuario usuario = registrarUsuarioUseCase.actualizar(
                id, request.getNombre(), request.getCorreo(), request.getRol(), authentication.getName());
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    /** Activa o desactiva un usuario (borrado logico). */
    @PatchMapping("/{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> cambiarEstado(@PathVariable Long id,
            @Valid @RequestBody CambiarEstadoRequestDTO request, Authentication authentication) {
        Usuario usuario = registrarUsuarioUseCase.cambiarEstado(
                id, request.getActivo(), authentication.getName());
        return ResponseEntity.ok(UsuarioMapper.toResponse(usuario));
    }

    /** Restablece la contrasena del usuario indicado (la asigna el ADMIN). */
    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Void> restablecerContrasena(@PathVariable Long id,
            @Valid @RequestBody RestablecerContrasenaRequestDTO request) {
        registrarUsuarioUseCase.restablecerContrasena(id, request.getContrasena());
        return ResponseEntity.noContent().build();
    }
}
