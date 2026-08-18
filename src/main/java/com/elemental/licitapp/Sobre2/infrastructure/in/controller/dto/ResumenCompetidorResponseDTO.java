package com.elemental.licitapp.Sobre2.infrastructure.in.controller.dto;

import java.math.BigDecimal;

public record ResumenCompetidorResponseDTO(
        String nombreOferente,
        long procesos,
        BigDecimal porcentajePromedio,
        BigDecimal porcentajeMinimo,
        BigDecimal porcentajeMaximo
) {
}
