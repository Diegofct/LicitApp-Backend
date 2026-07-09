package com.elemental.licitapp.Exception;

/** Ya existe un cuadro de obra para el número de proceso indicado. Se mapea a 409. */
public class ProcesoYaRegistradoException extends RuntimeException {
    public ProcesoYaRegistradoException(String message) {
        super(message);
    }
}
