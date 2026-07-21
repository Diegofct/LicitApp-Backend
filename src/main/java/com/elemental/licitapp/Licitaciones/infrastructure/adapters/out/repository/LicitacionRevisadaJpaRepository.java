package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.repository;

import com.elemental.licitapp.Licitaciones.domain.entity.LicitacionRevisada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LicitacionRevisadaJpaRepository extends JpaRepository<LicitacionRevisada, String> {

    @Query("select l.idDelProceso from LicitacionRevisada l")
    List<String> obtenerIdsRevisados();
}
