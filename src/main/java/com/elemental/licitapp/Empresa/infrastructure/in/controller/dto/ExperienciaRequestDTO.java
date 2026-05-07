package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienciaRequestDTO {

    private String contratista;
    private String entidadContratante;

    @PositiveOrZero(message = "valorSMMLV no puede ser negativo")
    private Double valorSMMLV;

    @PositiveOrZero(message = "porcentajeParticipacion no puede ser negativo")
    private Double porcentajeParticipacion;

    private List<String> codigosUNSPSC;
}
