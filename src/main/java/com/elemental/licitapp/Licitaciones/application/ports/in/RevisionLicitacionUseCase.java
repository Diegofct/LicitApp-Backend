package com.elemental.licitapp.Licitaciones.application.ports.in;

import java.util.List;

/**
 * Puerto de entrada para la marca "Revisado" de la Busqueda SECOP. El estado es un
 * conjunto compartido de idDelProceso; marcar y desmarcar son idempotentes.
 */
public interface RevisionLicitacionUseCase {

    /** idDelProceso de todas las licitaciones marcadas como revisadas. */
    List<String> obtenerRevisiones();

    /** Marca una licitacion como revisada. Si ya lo estaba, no hace nada. */
    void marcarRevisada(String idDelProceso);

    /** Quita la marca de revisada. Si no lo estaba, no hace nada. */
    void desmarcarRevisada(String idDelProceso);
}
