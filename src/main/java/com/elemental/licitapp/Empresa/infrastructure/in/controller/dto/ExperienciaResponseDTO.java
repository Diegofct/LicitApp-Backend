package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExperienciaResponseDTO {

    private Long id;
    private String contratista;
    private String entidadContratante;
    private Double valorSMMLV;
    private Double porcentajeParticipacion;
    private List<String> codigosUNSPSC;
}
