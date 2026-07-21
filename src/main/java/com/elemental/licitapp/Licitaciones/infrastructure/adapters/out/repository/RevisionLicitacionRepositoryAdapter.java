package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.repository;

import com.elemental.licitapp.Licitaciones.application.ports.out.RevisionLicitacionRepositoryPort;
import com.elemental.licitapp.Licitaciones.domain.entity.LicitacionRevisada;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RevisionLicitacionRepositoryAdapter implements RevisionLicitacionRepositoryPort {

    private final LicitacionRevisadaJpaRepository jpaRepository;

    public RevisionLicitacionRepositoryAdapter(LicitacionRevisadaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<String> obtenerIdsRevisados() {
        return jpaRepository.obtenerIdsRevisados();
    }

    @Override
    public boolean existe(String idDelProceso) {
        return jpaRepository.existsById(idDelProceso);
    }

    @Override
    public void marcar(String idDelProceso) {
        jpaRepository.save(new LicitacionRevisada(idDelProceso));
    }

    @Override
    public void desmarcar(String idDelProceso) {
        jpaRepository.deleteById(idDelProceso);
    }
}
