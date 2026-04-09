package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RequisitoLicitacionJpaRepository extends JpaRepository<RequisitoLicitacion, Long> {
    Optional<RequisitoLicitacion> findByCuadroDeObraId(Long cuadroDeObraId);
    void deleteByCuadroDeObraId(Long cuadroDeObraId);
}
