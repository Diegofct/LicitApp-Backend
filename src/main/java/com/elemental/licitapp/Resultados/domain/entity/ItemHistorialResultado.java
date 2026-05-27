package com.elemental.licitapp.Resultados.domain.entity;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemHistorialResultado(
        Long id,
        String numeroProceso,
        String entidadContratante,
        String descripcionObjeto,
        BigDecimal monto,
        CuadroDeObraEstado estado,
        String observacion,
        LocalDateTime fechaPublicacion,
        LocalDateTime fechaCierre
) {
}
