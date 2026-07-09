package com.elemental.licitapp.CuadroDeObra.application.ports.out;

import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface RequisitoLicitacionRepositoryPort {
    RequisitoLicitacion save(RequisitoLicitacion requisito);
    Optional<RequisitoLicitacion> findByCuadroDeObraId(Long cuadroDeObraId);
    void deleteByCuadroDeObraId(Long cuadroDeObraId);

    /** (RF3) Subconjunto de {@code ids} cuyos cuadros ya tienen requisitos guardados. */
    Set<Long> idsConRequisitos(Collection<Long> ids);
}
