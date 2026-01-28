package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.repository;

import com.elemental.licitapp.Licitaciones.application.ports.out.LicitacionRepositoryPort;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class LicitacionRepositoryAdapter implements LicitacionRepositoryPort {

    private final LicitacionJpaRepository licitacionJpaRepository;

    @Override
    public Optional<Licitacion> findById(Long id) {
        return licitacionJpaRepository.findById(id);
    }

    @Override
    public List<Licitacion> findAll() {
        return licitacionJpaRepository.findAll();
    }
}
