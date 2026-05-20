package com.elemental.licitapp.SeguimientoProceso.infrastructure.out.repository;

import com.elemental.licitapp.SeguimientoProceso.application.ports.out.SeguimientoRepositoryPort;
import com.elemental.licitapp.SeguimientoProceso.domain.entity.SeguimientoProceso;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SeguimientoRepositoryAdapter implements SeguimientoRepositoryPort {

    private final SeguimientoProcesoJpaRepository jpaRepository;

    public SeguimientoRepositoryAdapter(SeguimientoProcesoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SeguimientoProceso guardar(SeguimientoProceso seguimiento) {
        return jpaRepository.save(seguimiento);
    }

    @Override
    public Optional<SeguimientoProceso> buscarPorCuadroDeObra(Long cuadroDeObraId) {
        return jpaRepository.findByCuadroDeObraId(cuadroDeObraId);
    }
}
