package com.elemental.licitapp.Licitaciones.application.ports.out;

import com.elemental.licitapp.Licitaciones.domain.entity.DocumentoProceso;

import java.util.List;

public interface DocumentosSecopPort {

    /**
     * Documentos publicados en un proceso, buscados por su identificador de portafolio
     * ({@code CO1.BDOS.*}). Devuelve una lista vacía cuando el proceso aún no tiene
     * archivos publicados: eso no es un error.
     */
    List<DocumentoProceso> obtenerDocumentos(String idDelPortafolio);
}
