package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import com.elemental.licitapp.Empresa.domain.entity.Empresa;

import java.math.BigDecimal;

/**
 * Value object que asocia una empresa con su porcentaje de participación efectivo
 * en una evaluación de consorcio/UT. El porcentaje se expresa como fracción en el
 * rango (0,1] (ej. 0.5 = 50%). La suma de porcentajes de todos los integrantes de
 * una evaluación debe ser 1 (con tolerancia de redondeo). La validación de esa
 * invariante es responsabilidad del use case que construye la lista.
 */
public record IntegranteEvaluacion(
    Empresa empresa,
    BigDecimal porcentaje
) {}
