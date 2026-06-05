package com.elemental.licitapp.Seguridad.infrastructure.out.repository;

import com.elemental.licitapp.Seguridad.application.ports.out.UsuarioRepositoryPort;
import com.elemental.licitapp.Seguridad.domain.entity.Usuario;
import com.elemental.licitapp.Seguridad.domain.enums.Rol;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Usuario guardar(Usuario usuario) {
        return usuarioJpaRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioJpaRepository.findByCorreo(correo);
    }

    @Override
    public boolean existePorCorreo(String correo) {
        return usuarioJpaRepository.existsByCorreo(correo);
    }

    @Override
    public boolean existeAlgunAdmin() {
        return usuarioJpaRepository.existsByRol(Rol.ADMIN);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioJpaRepository.findAll();
    }
}
