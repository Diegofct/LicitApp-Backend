package com.elemental.licitapp.InteligenciaArtificial.application.ports.in;

import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccionPliego;

/**
 * Puerto de entrada PUBLICO del slice InteligenciaArtificial: lo consume CuadroDeObra a
 * traves de un adaptador de salida. Extrae los requisitos habilitantes de un pliego en PDF
 * (ya recortado a las paginas relevantes por el llamador) y devuelve un borrador validado;
 * nunca persiste.
 */
public interface ExtraerRequisitosPliegoUseCase {

    /**
     * @param pdfEnsamblado  PDF que contiene SOLO las paginas relevantes del pliego
     *                       (el recorte lo hace el llamador para controlar el costo)
     * @param nombreArchivo  nombre original del archivo, solo para mensajes/logs
     * @return borrador de requisitos extraidos + advertencias
     */
    ResultadoExtraccionPliego extraer(byte[] pdfEnsamblado, String nombreArchivo);
}
