package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoRequestDTO {

    @NotNull(message = "cuadroDeObraEstado es obligatorio")
    private CuadroDeObraEstado cuadroDeObraEstado;
}
