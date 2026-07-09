package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.mapper;

import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.ExtraccionPliegoResponseDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.RequisitoLicitacionRequestDTO;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.RequisitosPliegoExtraidos;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccionPliego;

import java.math.BigDecimal;

/**
 * Mapea el resultado de extraccion del slice IA al borrador que consume el front,
 * reutilizando {@link RequisitoLicitacionRequestDTO} (estilo {@code CuadroDeObraRequestMapper},
 * metodos estaticos, sin MapStruct).
 */
public final class ExtraccionPliegoResponseMapper {

    private ExtraccionPliegoResponseMapper() {}

    public static ExtraccionPliegoResponseDTO toResponse(ResultadoExtraccionPliego resultado) {
        RequisitosPliegoExtraidos d = resultado.datos();

        RequisitoLicitacionRequestDTO requisitos = RequisitoLicitacionRequestDTO.builder()
                .general(d.general())
                .especifica1(d.especifica1())
                .especifica2(d.especifica2())
                .secundaria(d.secundaria())
                .contrato(d.contrato())
                .presupuesto(aDecimal(d.presupuesto()))
                .patrimonio(aDecimal(d.patrimonio()))
                .capitalTrabajo(aDecimal(d.capitalTrabajo()))
                .liquidez(aDecimal(d.liquidez()))
                .endeudamiento(aDecimal(d.endeudamiento()))
                .razonCoberturaInteres(aDecimal(d.razonCoberturaInteres()))
                .rentabilidadPatrimonio(aDecimal(d.rentabilidadPatrimonio()))
                .rentabilidadActivo(aDecimal(d.rentabilidadActivo()))
                .kResidualProceso(aDecimal(d.kResidualProceso()))
                .poeAnticipo(d.poeAnticipo())
                .build();

        return ExtraccionPliegoResponseDTO.builder()
                .requisitos(requisitos)
                .advertencias(resultado.advertencias())
                .build();
    }

    /**
     * Convierte el valor crudo (Double) que entrega el LLM al BigDecimal del DTO de
     * requisitos. Se hace en el borde para no alterar el contrato/schema del slice IA.
     * BigDecimal.valueOf usa Double.toString, evitando artefactos de representacion binaria.
     */
    private static BigDecimal aDecimal(Double valor) {
        return valor != null ? BigDecimal.valueOf(valor) : null;
    }
}
