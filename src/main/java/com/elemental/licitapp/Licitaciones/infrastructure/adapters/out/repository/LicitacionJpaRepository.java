package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.repository;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LicitacionJpaRepository extends JpaRepository<Licitacion, Long> {
}
