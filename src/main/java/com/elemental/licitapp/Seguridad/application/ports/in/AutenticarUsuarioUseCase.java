package com.elemental.licitapp.Seguridad.application.ports.in;

/**
 * Autenticacion por correo + contrasena. Devuelve un token de acceso JWT
 * firmado si las credenciales son validas; de lo contrario lanza
 * CredencialesInvalidasException.
 */
public interface AutenticarUsuarioUseCase {

    ResultadoAutenticacion autenticar(String correo, String contrasenaEnClaro);

    /**
     * Datos que el frontend necesita tras el login: el token y los datos
     * publicos del usuario autenticado.
     */
    record ResultadoAutenticacion(
            String token,
            long expiraEnSegundos,
            Long usuarioId,
            String nombre,
            String correo,
            String rol
    ) {}
}
