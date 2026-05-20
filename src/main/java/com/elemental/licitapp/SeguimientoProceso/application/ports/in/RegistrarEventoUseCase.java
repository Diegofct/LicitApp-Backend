package com.elemental.licitapp.SeguimientoProceso.application.ports.in;

import com.elemental.licitapp.SeguimientoProceso.domain.entity.EventoSeguimiento;

public interface RegistrarEventoUseCase {
    EventoSeguimiento registrar(Long cuadroDeObraId, EventoSeguimiento evento);
}
