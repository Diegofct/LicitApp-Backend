package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsorcioResponseDTO {
    private Long id;
    private Long cuadroDeObraId;
    private TipoParticipacion tipoParticipacion;
    private LocalDateTime fechaConformacion;
    private String observaciones;
    private List<IntegranteConsorcioResponseDTO> integrantes;
}
