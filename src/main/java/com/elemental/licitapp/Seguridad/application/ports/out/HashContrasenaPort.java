package com.elemental.licitapp.Seguridad.application.ports.out;

/**
 * Puerto de salida que aisla el algoritmo de hashing (BCrypt) del servicio de
 * aplicacion. Permite cambiar la implementacion sin tocar la logica de negocio.
 */
public interface HashContrasenaPort {

    String hashear(String contrasenaEnClaro);

    boolean coincide(String contrasenaEnClaro, String hashAlmacenado);
}
