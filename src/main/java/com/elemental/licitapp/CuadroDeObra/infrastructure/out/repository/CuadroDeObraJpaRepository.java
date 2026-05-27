package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CuadroDeObraJpaRepository extends JpaRepository<CuadroDeObra, Long> {
    Page<CuadroDeObra> findByCuadroDeObraEstadoIn(List<CuadroDeObraEstado> estados, Pageable pageable);

    @Query("select c.cuadroDeObraEstado, count(c) from CuadroDeObra c group by c.cuadroDeObraEstado")
    List<Object[]> contarAgrupadoPorEstado();
}
