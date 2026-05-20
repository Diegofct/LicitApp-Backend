package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.application.ports.out.InicializarSeguimientoPort;
import com.elemental.licitapp.SeguimientoProceso.application.ports.in.InicializarSeguimientoUseCase;
import org.springframework.stereotype.Component;

@Component
public class SeguimientoModuloAdapter implements InicializarSeguimientoPort {

    private final InicializarSeguimientoUseCase inicializarSeguimientoUseCase;

    public SeguimientoModuloAdapter(InicializarSeguimientoUseCase inicializarSeguimientoUseCase) {
        this.inicializarSeguimientoUseCase = inicializarSeguimientoUseCase;
    }

    @Override
    public void inicializar(Long cuadroDeObraId) {
        inicializarSeguimientoUseCase.inicializar(cuadroDeObraId);
    }
}
