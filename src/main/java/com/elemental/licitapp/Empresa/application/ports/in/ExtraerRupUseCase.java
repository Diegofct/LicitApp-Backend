package com.elemental.licitapp.Empresa.application.ports.in;

import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccion;

/**
 * Puerto de entrada de Empresa para la extraccion de un RUP. El controller depende de
 * esta interfaz (no del puerto de salida), manteniendo limpia la direccion de dependencias.
 */
public interface ExtraerRupUseCase {

    ResultadoExtraccion extraerDatosRup(byte[] pdf, String nombreArchivo);
}
