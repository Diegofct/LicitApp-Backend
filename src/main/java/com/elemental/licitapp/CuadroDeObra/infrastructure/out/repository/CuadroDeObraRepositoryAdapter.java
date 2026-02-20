package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.application.ports.out.CuadroDeObraRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CuadroDeObraRepositoryAdapter implements CuadroDeObraRepositoryPort {

    private final CuadroDeObraJpaRepository jpaRepository;

    public CuadroDeObraRepositoryAdapter(CuadroDeObraJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CuadroDeObra save(CuadroDeObra c) {
        return jpaRepository.save(c);
    }

    @Override
    public Optional<CuadroDeObra> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<CuadroDeObra> findAll() {
        return List.of();
    }

    @Override
    public Page<CuadroDeObra> findByCuadroDeObraEstadoIn(List<CuadroDeObraEstado> estados, Pageable pageable) {
        return jpaRepository.findByCuadroDeObraEstadoIn(estados, pageable);
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }
}
