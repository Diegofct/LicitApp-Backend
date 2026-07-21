package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.CuadroDeObra.domain.enums.PresentacionMarca;
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

    /** Identidad del proceso en SECOP II; null si el cuadro se cargó a mano. */
    private String idDelProceso;

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
    private Integer plazo;
    private Integer anticipo;
    private String observacion;
    private CuadroDeObraEstado cuadroDeObraEstado;

    /** Marca "nos presentamos" compartida; null = sin marca. */
    private PresentacionMarca presentacion;

    /** (RF3) true si el proceso ya tiene requisitos de licitación guardados. */
    private boolean tieneRequisitos;
}
