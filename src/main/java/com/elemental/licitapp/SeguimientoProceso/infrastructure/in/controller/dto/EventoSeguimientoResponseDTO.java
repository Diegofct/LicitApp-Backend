package com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto;

import com.elemental.licitapp.SeguimientoProceso.domain.enums.TipoEventoSeguimiento;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventoSeguimientoResponseDTO {
    private Long id;
    private TipoEventoSeguimiento tipo;
    private LocalDateTime fechaEvento;
    private String descripcion;
    private LocalDateTime fechaRegistro;
}
