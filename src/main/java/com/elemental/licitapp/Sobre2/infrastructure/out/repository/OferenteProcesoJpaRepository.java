package com.elemental.licitapp.Sobre2.infrastructure.out.repository;

import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OferenteProcesoJpaRepository extends JpaRepository<OferenteProceso, Long> {

    List<OferenteProceso> findByCuadroDeObraIdOrderByValorOfertaAsc(Long cuadroDeObraId);

    Optional<OferenteProceso> findByCuadroDeObraIdAndIdentificadorOferta(Long cuadroDeObraId,
                                                                        String identificadorOferta);

    /**
     * Inteligencia de competidores: agrega los porcentajes de un mismo oferente a traves de
     * todos los procesos importados. Se agrupa por nombre porque los consorcios y UT no
     * publican NIT utilizable en SECOP II.
     */
    @Query("""
            SELECT o.nombreOferente,
                   COUNT(DISTINCT o.cuadroDeObraId),
                   AVG(o.porcentaje),
                   MIN(o.porcentaje),
                   MAX(o.porcentaje)
            FROM OferenteProceso o
            WHERE o.porcentaje IS NOT NULL
              AND (:nombre IS NULL OR LOWER(o.nombreOferente) LIKE LOWER(CONCAT('%', :nombre, '%')))
            GROUP BY o.nombreOferente
            ORDER BY COUNT(DISTINCT o.cuadroDeObraId) DESC, o.nombreOferente ASC
            """)
    List<Object[]> agregarPorCompetidor(@Param("nombre") String nombre);
}
