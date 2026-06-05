package com.elemental.licitapp.Exception;

/** Correo o contrasena incorrectos, o usuario inactivo. Se mapea a 401. */
public class CredencialesInvalidasException extends RuntimeException {
    public CredencialesInvalidasException(String message) {
        super(message);
    }
}
