package com.elemental.licitapp.Seguridad.application.service;

import com.elemental.licitapp.Exception.CorreoYaRegistradoException;
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

    private static String normalizar(String correo) {
        return correo == null ? null : correo.trim().toLowerCase();
    }
}
