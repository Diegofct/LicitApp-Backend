package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuardarConsorcioRequestDTO {

    @NotNull(message = "cuadroDeObraId es obligatorio")
    @Positive(message = "cuadroDeObraId debe ser positivo")
    private Long cuadroDeObraId;

    @NotNull(message = "tipoParticipacion es obligatorio")
    private TipoParticipacion tipoParticipacion;

    private String observaciones;

    @NotEmpty(message = "Debe haber al menos un integrante")
    @Valid
    private List<IntegranteConsorcioRequestDTO> integrantes;
}
