package com.elemental.licitapp.Seguridad.infrastructure.out.repository;

import com.elemental.licitapp.Seguridad.domain.entity.Usuario;
import com.elemental.licitapp.Seguridad.domain.enums.Rol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    boolean existsByRol(Rol rol);
}
