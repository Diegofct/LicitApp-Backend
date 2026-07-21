package com.elemental.licitapp.Seguridad.application.service;

import com.elemental.licitapp.Exception.CorreoYaRegistradoException;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import com.elemental.licitapp.Seguridad.application.ports.in.RegistrarUsuarioUseCase;
import com.elemental.licitapp.Seguridad.application.ports.out.HashContrasenaPort;
import com.elemental.licitapp.Seguridad.application.ports.out.UsuarioRepositoryPort;
import com.elemental.licitapp.Seguridad.domain.entity.Usuario;
import com.elemental.licitapp.Seguridad.domain.enums.Rol;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RegistroUsuarioService implements RegistrarUsuarioUseCase {

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final HashContrasenaPort hashContrasenaPort;

    public RegistroUsuarioService(UsuarioRepositoryPort usuarioRepositoryPort,
                                  HashContrasenaPort hashContrasenaPort) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.hashContrasenaPort = hashContrasenaPort;
    }

    @Override
    @Transactional
    public Usuario registrar(String nombre, String correo, String contrasenaEnClaro, Rol rol) {
        String correoNormalizado = normalizar(correo);
        if (usuarioRepositoryPort.existePorCorreo(correoNormalizado)) {
            throw new CorreoYaRegistradoException("Ya existe un usuario con el correo: " + correoNormalizado);
        }

        Usuario usuario = Usuario.builder()
                .nombre(nombre)
                .correo(correoNormalizado)
                .passwordHash(hashContrasenaPort.hashear(contrasenaEnClaro))
                .rol(rol)
                .activo(true)
                .fechaCreacion(LocalDateTime.now())
                .build();

        return usuarioRepositoryPort.guardar(usuario);
    }

    @Override
    public List<Usuario> listar() {
        return usuarioRepositoryPort.listarTodos();
    }

    @Override
    public Optional<Usuario> obtenerPorCorreo(String correo) {
        return usuarioRepositoryPort.buscarPorCorreo(normalizar(correo));
    }

    @Override
    @Transactional
    public Usuario actualizar(Long id, String nombre, String correo, Rol rol, String correoSolicitante) {
        Usuario usuario = obtenerOError(id);
        String correoNormalizado = normalizar(correo);

        // El correo no puede pertenecer a OTRO usuario (mantenerlo igual es valido).
        usuarioRepositoryPort.buscarPorCorreo(correoNormalizado)
                .filter(otro -> !otro.getId().equals(id))
                .ifPresent(otro -> {
                    throw new CorreoYaRegistradoException("Ya existe un usuario con el correo: " + correoNormalizado);
                });

        boolean esElMismo = esSolicitante(usuario, correoSolicitante);
        boolean degradaAdmin = usuario.getRol() == Rol.ADMIN && rol != Rol.ADMIN;

        // RF9: un ADMIN no puede degradar su propio rol.
        if (esElMismo && degradaAdmin) {
            throw new IllegalArgumentException("No puedes cambiar tu propio rol de administrador.");
        }
        // RF10: debe quedar al menos un ADMIN activo.
        if (degradaAdmin && usuario.isActivo() && usuarioRepositoryPort.contarAdminsActivos() <= 1) {
            throw new IllegalArgumentException("Debe existir al menos un administrador activo.");
        }

        usuario.setNombre(nombre);
        usuario.setCorreo(correoNormalizado);
        usuario.setRol(rol);
        usuario.setFechaActualizacion(LocalDateTime.now());
        return usuarioRepositoryPort.guardar(usuario);
    }

    @Override
    @Transactional
    public Usuario cambiarEstado(Long id, boolean activo, String correoSolicitante) {
        Usuario usuario = obtenerOError(id);

        if (!activo) {
            // RF9: no puedes desactivar tu propia cuenta.
            if (esSolicitante(usuario, correoSolicitante)) {
                throw new IllegalArgumentException("No puedes desactivar tu propia cuenta.");
            }
            // RF10: no dejes al sistema sin ADMIN activo.
            if (usuario.getRol() == Rol.ADMIN && usuario.isActivo()
                    && usuarioRepositoryPort.contarAdminsActivos() <= 1) {
                throw new IllegalArgumentException("Debe existir al menos un administrador activo.");
            }
        }

        usuario.setActivo(activo);
        usuario.setFechaActualizacion(LocalDateTime.now());
        return usuarioRepositoryPort.guardar(usuario);
    }

    @Override
    @Transactional
    public void restablecerContrasena(Long id, String nuevaContrasenaEnClaro) {
        Usuario usuario = obtenerOError(id);
        usuario.setPasswordHash(hashContrasenaPort.hashear(nuevaContrasenaEnClaro));
        usuario.setFechaActualizacion(LocalDateTime.now());
        usuarioRepositoryPort.guardar(usuario);
    }

    private Usuario obtenerOError(Long id) {
        return usuarioRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado: " + id));
    }

    private boolean esSolicitante(Usuario usuario, String correoSolicitante) {
        String solicitante = normalizar(correoSolicitante);
        return solicitante != null && solicitante.equals(usuario.getCorreo());
    }

    private static String normalizar(String correo) {
        return correo == null ? null : correo.trim().toLowerCase();
    }
}
