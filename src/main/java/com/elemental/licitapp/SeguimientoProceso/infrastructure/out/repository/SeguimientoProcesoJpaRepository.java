package com.elemental.licitapp.SeguimientoProceso.infrastructure.out.repository;

import com.elemental.licitapp.SeguimientoProceso.domain.entity.SeguimientoProceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeguimientoProcesoJpaRepository extends JpaRepository<SeguimientoProceso, Long> {
    Optional<SeguimientoProceso> findByCuadroDeObraId(Long cuadroDeObraId);
}
