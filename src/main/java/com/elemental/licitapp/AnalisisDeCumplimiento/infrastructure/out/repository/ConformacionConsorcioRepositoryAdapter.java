package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.out.repository;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ConformacionConsorcioRepositoryPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ConformacionConsorcioRepositoryAdapter implements ConformacionConsorcioRepositoryPort {

    private final ConformacionConsorcioJpaRepository jpaRepository;

    public ConformacionConsorcioRepositoryAdapter(ConformacionConsorcioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ConformacionConsorcio guardar(ConformacionConsorcio conformacion) {
        return jpaRepository.save(conformacion);
    }

    @Override
    public Optional<ConformacionConsorcio> buscarPorCuadroDeObra(Long cuadroDeObraId) {
        return jpaRepository.findByCuadroDeObraId(cuadroDeObraId);
    }
}
