package com.elemental.licitapp.Seguridad.application.ports.out;

import com.elemental.licitapp.Seguridad.domain.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepositoryPort {
    Usuario guardar(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    Optional<Usuario> buscarPorCorreo(String correo);
    boolean existePorCorreo(String correo);
    boolean existeAlgunAdmin();
    /** Cantidad de usuarios ADMIN actualmente activos (para no quedarse sin administradores). */
    long contarAdminsActivos();
    List<Usuario> listarTodos();
}
