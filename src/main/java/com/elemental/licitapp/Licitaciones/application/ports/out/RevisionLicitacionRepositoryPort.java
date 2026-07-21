package com.elemental.licitapp.Licitaciones.application.ports.out;

import java.util.List;

/**
 * Puerto de salida para persistir el conjunto de licitaciones revisadas.
 */
public interface RevisionLicitacionRepositoryPort {

    List<String> obtenerIdsRevisados();
    boolean existe(String idDelProceso);
    void marcar(String idDelProceso);
    void desmarcar(String idDelProceso);
}
