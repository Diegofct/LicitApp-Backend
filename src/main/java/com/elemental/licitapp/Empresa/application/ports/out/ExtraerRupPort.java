package com.elemental.licitapp.Empresa.application.ports.out;

import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccion;

/**
 * Puerto de salida de Empresa hacia el slice InteligenciaArtificial. Lo implementa
 * {@code InteligenciaArtificialModuloAdapter}, que delega en el puerto de entrada publico
 * del slice IA. Mismo patron con que Empresa/otros slices consumen capacidades ajenas.
 */
public interface ExtraerRupPort {

    ResultadoExtraccion extraer(byte[] pdf, String nombreArchivo);
}
