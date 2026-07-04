package com.elemental.licitapp.CuadroDeObra.application.ports.out;

import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccionPliego;

/**
 * Frontera de CuadroDeObra con el slice InteligenciaArtificial. El adaptador que lo
 * implementa ({@code InteligenciaArtificialModuloAdapter}) consume el puerto de entrada
 * publico de IA, sin conocer su repositorio ni el proveedor LLM.
 */
public interface ExtraerPliegoPort {

    /**
     * @param pdfEnsamblado PDF con SOLO las paginas relevantes del pliego (ya recortado)
     * @param nombreArchivo nombre original del archivo, para mensajes/logs
     */
    ResultadoExtraccionPliego extraer(byte[] pdfEnsamblado, String nombreArchivo);
}
