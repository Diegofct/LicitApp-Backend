package com.elemental.licitapp.InteligenciaArtificial.domain.entity;

import java.util.List;

/**
 * Resultado de una extraccion: el borrador de datos mas las advertencias generadas
 * durante la validacion (campos nulos, descuadres en la verificacion cruzada financiera,
 * etc.). El analista revisa las advertencias antes de confirmar la persistencia.
 */
public record ResultadoExtraccion(
        DatosRupExtraidos datos,
        List<String> advertencias
) {
}
