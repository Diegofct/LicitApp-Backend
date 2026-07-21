package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
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
public class CuadroDeObraRequestDTO {

    @NotBlank(message = "entidadContratante es obligatorio")
    private String entidadContratante;

    @NotBlank(message = "numeroProceso es obligatorio")
    private String numeroProceso;

    /**
     * Identificador del proceso en SECOP II. Opcional: los cuadros cargados a mano no
     * provienen de SECOP y se crean sin él. Cuando viene, es la llave que identifica el
     * proceso y sobre la que se rechazan los duplicados.
     */
    private String idDelProceso;

    private String descripcionObjeto;

    private String estadoProceso;

    private LocalDateTime fechaPublicacion;

    private LocalDateTime fechaCierre;

    @PositiveOrZero(message = "monto no puede ser negativo")
    private BigDecimal monto;

    @PositiveOrZero(message = "valorSMMLV no puede ser negativo")
    private Double valorSMMLV;

    private String tipoProyecto;

    private String departamento;

    private String municipio;

    private String experiencia;

    @PositiveOrZero(message = "plazo no puede ser negativo")
    private Integer plazo;

    @PositiveOrZero(message = "anticipo no puede ser negativo")
    private Integer anticipo;

    private String observacion;

    private CuadroDeObraEstado cuadroDeObraEstado;
}
