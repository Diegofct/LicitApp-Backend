package com.elemental.licitapp.Seguridad.application.ports.in;

import com.elemental.licitapp.Seguridad.domain.entity.Usuario;
import com.elemental.licitapp.Seguridad.domain.enums.Rol;

import java.util.List;
import java.util.Optional;

/**
 * Alta y consulta de usuarios. Es una operacion restringida a ADMIN (la
 * autorizacion se aplica en la capa web/SecurityConfig, no aqui).
 */
public interface RegistrarUsuarioUseCase {

    /**
     * Crea un usuario con la contrasena en claro (se hashea internamente).
     * @return el usuario persistido (sin exponer la contrasena en claro).
     */
    Usuario registrar(String nombre, String correo, String contrasenaEnClaro, Rol rol);

    List<Usuario> listar();

    /** Usado por /auth/me para rehidratar la sesion del usuario autenticado. */
    Optional<Usuario> obtenerPorCorreo(String correo);
}
