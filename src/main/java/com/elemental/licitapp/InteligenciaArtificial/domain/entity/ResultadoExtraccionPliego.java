package com.elemental.licitapp.InteligenciaArtificial.domain.entity;

import java.util.List;

/**
 * Resultado de la extraccion de un pliego: el borrador de requisitos habilitantes mas las
 * advertencias generadas durante la validacion (campos nulos, indicadores fuera de rango,
 * porcentajes normalizados, etc.). El analista revisa las advertencias antes de confirmar.
 */
public record ResultadoExtraccionPliego(
        RequisitosPliegoExtraidos datos,
        List<String> advertencias
) {
}
