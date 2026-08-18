package com.elemental.licitapp.Sobre2.application.ports.out;

import com.elemental.licitapp.Sobre2.domain.entity.CriterioBusquedaOfertas;
import com.elemental.licitapp.Sobre2.domain.entity.OfertaImportada;

import java.util.List;

/** Frontera con el dataset de ofertas por proceso de la API de datos abiertos de SECOP II. */
public interface OfertasSecopPort {

    /**
     * Ofertas del proceso, ya deduplicadas por oferta (SECOP publica una fila por integrante
     * del consorcio) y con el texto libre reparado.
     *
     * @param criterio  como se identifica el proceso: por identificador (exacto) o por
     *                  referencia (aproximado). Ver {@link CriterioBusquedaOfertas}
     * @param historico true para el dataset anterior a 2024
     */
    List<OfertaImportada> buscarOfertas(CriterioBusquedaOfertas criterio, boolean historico);
}
