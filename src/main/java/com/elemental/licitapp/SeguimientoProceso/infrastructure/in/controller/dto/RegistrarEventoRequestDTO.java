package com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto;

import com.elemental.licitapp.SeguimientoProceso.domain.enums.TipoEventoSeguimiento;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarEventoRequestDTO {

    @NotNull(message = "tipo es obligatorio")
    private TipoEventoSeguimiento tipo;

    @NotNull(message = "fechaEvento es obligatoria")
    private LocalDateTime fechaEvento;

    private String descripcion;
}
