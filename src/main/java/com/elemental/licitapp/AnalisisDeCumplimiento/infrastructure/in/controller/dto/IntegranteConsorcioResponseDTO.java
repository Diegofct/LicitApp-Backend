package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IntegranteConsorcioResponseDTO {
    private Long id;
    private Long empresaId;
    private BigDecimal porcentajeParticipacion;
}
