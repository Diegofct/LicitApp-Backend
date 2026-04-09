package com.elemental.licitapp.CuadroDeObra.application.ports.out;

import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import java.util.Optional;

public interface RequisitoLicitacionRepositoryPort {
    RequisitoLicitacion save(RequisitoLicitacion requisito);
    Optional<RequisitoLicitacion> findByCuadroDeObraId(Long cuadroDeObraId);
    void deleteByCuadroDeObraId(Long cuadroDeObraId);
}
