package com.elemental.licitapp.Resultados.infrastructure.in.controller.dto;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ItemHistorialResultadoResponseDTO(
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
