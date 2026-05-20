package com.elemental.licitapp.SeguimientoProceso.application.ports.out;

import com.elemental.licitapp.SeguimientoProceso.domain.entity.SeguimientoProceso;

import java.util.Optional;

public interface SeguimientoRepositoryPort {
    SeguimientoProceso guardar(SeguimientoProceso seguimiento);
    Optional<SeguimientoProceso> buscarPorCuadroDeObra(Long cuadroDeObraId);
}
