package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuadroDeObraResponseDTO {

    private Long id;
    private String entidadContratante;
    private String numeroProceso;
    private String descripcionObjeto;
    private String estadoProceso;
    private LocalDateTime fechaPublicacion;
    private LocalDateTime fechaCierre;
    private BigDecimal monto;

    @JsonProperty("valorSMMLV")
    private Double valorSMMLV;

    private String tipoProyecto;
    private String departamento;
    private String municipio;
    private String experiencia;
    private String plazo;
    private String anticipo;
    private String observacion;
    private CuadroDeObraEstado cuadroDeObraEstado;
}
