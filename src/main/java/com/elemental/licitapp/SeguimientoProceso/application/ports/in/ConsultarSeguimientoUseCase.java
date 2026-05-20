package com.elemental.licitapp.SeguimientoProceso.application.ports.in;

import com.elemental.licitapp.SeguimientoProceso.domain.entity.SeguimientoProceso;

import java.util.Optional;

public interface ConsultarSeguimientoUseCase {
    Optional<SeguimientoProceso> obtenerPorCuadroDeObra(Long cuadroDeObraId);
}
