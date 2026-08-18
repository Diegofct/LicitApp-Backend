package com.elemental.licitapp.Sobre2.domain.entity;

import java.math.BigDecimal;

/**
 * Inteligencia acumulada sobre un competidor a traves de todos los procesos importados.
 * Se agrupa por nombre y no por NIT porque los consorcios y uniones temporales -que son la
 * mayoria en obra publica- no publican NIT utilizable en SECOP II.
 *
 * @param porcentajePromedio promedio de los porcentajes de oferta frente al presupuesto
 *                           oficial: es el indicador de que tan agresivo suele ser
 */
public record ResumenCompetidor(
        String nombreOferente,
        long procesos,
        BigDecimal porcentajePromedio,
        BigDecimal porcentajeMinimo,
        BigDecimal porcentajeMaximo
) {
}
