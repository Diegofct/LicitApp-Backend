package com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto;

import com.elemental.licitapp.Sobre2.domain.enums.OrigenOferente;

import java.math.BigDecimal;
import java.time.LocalDate;

public record OferenteProcesoResponseDTO(
        Long id,
        Long cuadroDeObraId,
        String nombreOferente,
        String nitOferente,
        BigDecimal valorOferta,
        BigDecimal porcentaje,
        String moneda,
        LocalDate fechaRegistro,
        Boolean valida,
        OrigenOferente origen
) {
}
