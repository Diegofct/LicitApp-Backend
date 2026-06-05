package com.elemental.licitapp.Exception;

/** Ya existe un usuario con el correo indicado. Se mapea a 409. */
public class CorreoYaRegistradoException extends RuntimeException {
    public CorreoYaRegistradoException(String message) {
        super(message);
    }
}
