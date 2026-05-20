package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.out.repository;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConformacionConsorcioJpaRepository extends JpaRepository<ConformacionConsorcio, Long> {
    Optional<ConformacionConsorcio> findByCuadroDeObraId(Long cuadroDeObraId);
}
