package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.application.ports.out.RequisitoLicitacionRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
public class RequisitoLicitacionRepositoryAdapter implements RequisitoLicitacionRepositoryPort {

    private final RequisitoLicitacionJpaRepository repository;

    public RequisitoLicitacionRepositoryAdapter(RequisitoLicitacionJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public RequisitoLicitacion save(RequisitoLicitacion requisito) {
        return repository.save(requisito);
    }

    @Override
    public Optional<RequisitoLicitacion> findByCuadroDeObraId(Long cuadroDeObraId) {
        return repository.findByCuadroDeObraId(cuadroDeObraId);
    }

    @Override
    public void deleteByCuadroDeObraId(Long cuadroDeObraId) {
        repository.deleteByCuadroDeObraId(cuadroDeObraId);
    }
}
