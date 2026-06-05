package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import java.math.BigDecimal;
import java.util.List;

/**
 * Integrante de una {@link PropuestaConsorcio} sugerida automáticamente.
 *
 * @param empresaId         identificador de la empresa.
 * @param nit               NIT de la empresa.
 * @param razonSocial       razón social de la empresa.
 * @param porcentaje        participación asignada en el consorcio, como fracción en (0,1].
 *                          Para la empresa solicitante es el valor enviado desde el front;
 *                          para las candidatas es el reparto proporcional del complemento.
 * @param solicitante       true si es la empresa que originó el análisis (la no cumplía sola).
 * @param requisitosQueCubre nombres de los requisitos deficitarios en los que esta empresa
 *                          es fuerte individualmente (orientativo para el usuario).
 */
public record IntegrantePropuesto(
    Long empresaId,
    String nit,
    String razonSocial,
    BigDecimal porcentaje,
    boolean solicitante,
    List<String> requisitosQueCubre
) {}
