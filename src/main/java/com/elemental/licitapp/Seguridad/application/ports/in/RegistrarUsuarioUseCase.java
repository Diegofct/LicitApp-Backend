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

    /**
     * Actualiza nombre, correo y rol de un usuario existente. La contrasena NO se toca aqui.
     * @param correoSolicitante correo del ADMIN que ejecuta la accion (para evitar auto-degradarse).
     * @throws com.elemental.licitapp.Exception.ResourceNotFoundException si el id no existe.
     * @throws com.elemental.licitapp.Exception.CorreoYaRegistradoException si el correo pertenece a otro usuario.
     * @throws IllegalArgumentException si un ADMIN intenta degradar su propio rol o al ultimo ADMIN activo.
     */
    Usuario actualizar(Long id, String nombre, String correo, Rol rol, String correoSolicitante);

    /**
     * Activa o desactiva un usuario (borrado logico). Un usuario inactivo no puede iniciar sesion.
     * @param correoSolicitante correo del ADMIN que ejecuta la accion (no puede desactivarse a si mismo).
     * @throws com.elemental.licitapp.Exception.ResourceNotFoundException si el id no existe.
     * @throws IllegalArgumentException si se intenta desactivar la propia cuenta o al ultimo ADMIN activo.
     */
    Usuario cambiarEstado(Long id, boolean activo, String correoSolicitante);

    /**
     * Restablece la contrasena de un usuario (la asigna un ADMIN). Rehashea con BCrypt.
     * @throws com.elemental.licitapp.Exception.ResourceNotFoundException si el id no existe.
     */
    void restablecerContrasena(Long id, String nuevaContrasenaEnClaro);
}
