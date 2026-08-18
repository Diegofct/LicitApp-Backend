package com.elemental.licitapp.Sobre2.application.ports.in;

import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;

import java.util.List;

/**
 * Alta, edicion y borrado manual de oferentes. Indispensable: alrededor del 10% de los
 * procesos de licitacion publica de obra no tienen las ofertas publicadas en el dataset,
 * y el licitador si las ve en pantalla en el portal de SECOP II.
 */
public interface GestionarOferentesUseCase {

    List<OferenteProceso> listar(Long cuadroId);

    OferenteProceso crear(Long cuadroId, OferenteProceso oferente);

    OferenteProceso actualizar(Long oferenteId, OferenteProceso cambios);

    void eliminar(Long oferenteId);
}
