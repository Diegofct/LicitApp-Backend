package com.elemental.licitapp.InteligenciaArtificial.domain.entity;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

/**
 * Un contrato de experiencia inscrito en el RUP. Mapea 1:1 a la entidad {@code Experiencia}.
 */
public record ExperienciaExtraida(

        @JsonPropertyDescription("Contratista reportado en el contrato inscrito")
        String contratista,

        @JsonPropertyDescription("Entidad contratante del contrato")
        String entidadContratante,

        @JsonPropertyDescription("Valor del contrato expresado en SMMLV (salarios minimos mensuales legales vigentes)")
        Double valorSMMLV,

        @JsonPropertyDescription("Porcentaje de participacion del proponente en el contrato (100 si fue individual)")
        Double porcentajeParticipacion,

        @JsonPropertyDescription("Codigos UNSPSC del contrato (clasificador de bienes y servicios)")
        List<String> codigosUNSPSC
) {
}
