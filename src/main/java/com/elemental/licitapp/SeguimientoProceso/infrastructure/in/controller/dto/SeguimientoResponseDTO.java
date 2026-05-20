package com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto;

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
public class SeguimientoResponseDTO {
    private Long id;
    private Long cuadroDeObraId;
    private LocalDateTime fechaInicio;
    private List<EventoSeguimientoResponseDTO> eventos;
}
