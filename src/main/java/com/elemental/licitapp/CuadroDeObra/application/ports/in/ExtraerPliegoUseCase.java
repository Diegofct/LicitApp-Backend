package com.elemental.licitapp.CuadroDeObra.application.ports.in;

import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccionPliego;

/**
 * Caso de uso de CuadroDeObra para extraer con IA los requisitos habilitantes de un pliego.
 * Recorta el PDF a las paginas indicadas por el analista y delega la extraccion en el slice
 * InteligenciaArtificial. Devuelve un borrador editable; nunca persiste.
 */
public interface ExtraerPliegoUseCase {

    /**
     * @param cuadroId      cuadro de obra al que se asociaran los requisitos (debe existir)
     * @param pdf           pliego completo en bytes (se procesa en memoria y se descarta)
     * @param paginas       lista de paginas/rangos 1-based, ej. "20-22,50,116-118"
     * @param nombreArchivo nombre original del archivo, para mensajes/logs
     * @return borrador de requisitos extraidos + advertencias
     */
    ResultadoExtraccionPliego extraerRequisitos(Long cuadroId, byte[] pdf, String paginas, String nombreArchivo);
}
