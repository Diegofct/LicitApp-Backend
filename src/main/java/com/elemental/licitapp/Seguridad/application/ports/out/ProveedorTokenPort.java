package com.elemental.licitapp.Seguridad.application.ports.out;

import com.elemental.licitapp.Seguridad.domain.entity.Usuario;

import java.util.Optional;

/**
 * Puerto de salida que aisla la libreria JWT (jjwt). El servicio emite tokens y
 * el filtro de seguridad los valida sin conocer detalles de firma/parsing.
 */
public interface ProveedorTokenPort {

    /** Emite un token de acceso firmado para el usuario. */
    String generar(Usuario usuario);

    /** Segundos de validez del token emitido (para informarlo al cliente). */
    long expiracionEnSegundos();

    /** Valida la firma/expiracion y extrae los datos del sujeto. Vacio si es invalido. */
    Optional<DatosToken> validar(String token);

    /** Claims minimas que el filtro necesita para reconstruir la autenticacion. */
    record DatosToken(String correo, String rol) {}
}
