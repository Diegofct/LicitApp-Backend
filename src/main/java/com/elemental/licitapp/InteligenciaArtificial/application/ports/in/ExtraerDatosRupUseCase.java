package com.elemental.licitapp.InteligenciaArtificial.application.ports.in;

import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccion;

/**
 * Puerto de entrada PUBLICO del slice InteligenciaArtificial: lo consumen otros slices
 * (ej. Empresa) a traves de un adaptador de salida. Extrae datos de un RUP en PDF y
 * devuelve un borrador validado; nunca persiste.
 */
public interface ExtraerDatosRupUseCase {

    /**
     * @param pdf            contenido del RUP en bytes (se procesa en memoria y se descarta)
     * @param nombreArchivo  nombre original del archivo, solo para mensajes/logs
     * @return borrador de datos extraidos + advertencias
     */
    ResultadoExtraccion extraer(byte[] pdf, String nombreArchivo);
}
