package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface RequisitoLicitacionJpaRepository extends JpaRepository<RequisitoLicitacion, Long> {
    Optional<RequisitoLicitacion> findByCuadroDeObraId(Long cuadroDeObraId);
    void deleteByCuadroDeObraId(Long cuadroDeObraId);

    /** (RF3) Ids de cuadros que ya tienen requisitos, resuelto en una sola consulta. */
    @Query("select r.cuadroDeObra.id from RequisitoLicitacion r where r.cuadroDeObra.id in :ids")
    List<Long> findCuadroIdsWithRequisitos(@Param("ids") Collection<Long> ids);
}
