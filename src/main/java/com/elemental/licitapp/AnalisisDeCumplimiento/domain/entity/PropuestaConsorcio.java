package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import java.util.List;

/**
 * Propuesta de conformación de proponente plural (consorcio/UT) sugerida cuando una
 * empresa no cumple los requisitos del pliego por sí sola.
 *
 * <p>A diferencia de la antigua sugerencia plana (una empresa suelta), esta propuesta
 * describe un consorcio completo: la empresa solicitante más las candidatas que cubren
 * su déficit, con el porcentaje de participación asignado a cada una.</p>
 *
 * @param integrantes          empresas que conforman el consorcio (incluye la solicitante).
 * @param cumpleGlobal         true si el consorcio cumple todos los requisitos del pliego.
 * @param requisitosCubiertos  requisitos que la empresa solicitante no cumplía sola y que
 *                             el consorcio sí cubre.
 * @param requisitosPendientes requisitos que siguen sin cumplirse (vacío si la propuesta
 *                             es completamente viable).
 */
public record PropuestaConsorcio(
    List<IntegrantePropuesto> integrantes,
    boolean cumpleGlobal,
    List<String> requisitosCubiertos,
    List<String> requisitosPendientes
) {}
