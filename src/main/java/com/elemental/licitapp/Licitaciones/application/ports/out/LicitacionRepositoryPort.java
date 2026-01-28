package com.elemental.licitapp.Licitaciones.application.ports.out;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import java.util.List;
import java.util.Optional;

public interface LicitacionRepositoryPort {
    Optional<Licitacion> findById(Long id);
    List<Licitacion> findAll();
}
