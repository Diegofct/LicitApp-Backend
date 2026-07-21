package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.CuadroDeObra.domain.projection.CuadroDeObraRef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CuadroDeObraJpaRepository extends JpaRepository<CuadroDeObra, Long> {
    Page<CuadroDeObra> findByCuadroDeObraEstadoIn(List<CuadroDeObraEstado> estados, Pageable pageable);

    @Query("select c.cuadroDeObraEstado, count(c) from CuadroDeObra c group by c.cuadroDeObraEstado")
    List<Object[]> contarAgrupadoPorEstado();

    /**
     * Referencias livianas (id + numeroProceso + idDelProceso) de los cuadros que tienen
     * número de proceso registrado, para el cruce contra las licitaciones de SECOP II.
     * El cruce se hace por {@code idDelProceso}, que puede venir NULL en los cuadros
     * cargados a mano.
     */
    @Query("select new com.elemental.licitapp.CuadroDeObra.domain.projection.CuadroDeObraRef("
            + "c.id, c.numeroProceso, c.idDelProceso) "
            + "from CuadroDeObra c where c.numeroProceso is not null and c.numeroProceso <> ''")
    List<CuadroDeObraRef> obtenerReferencias();

    boolean existsByIdDelProceso(String idDelProceso);
}
