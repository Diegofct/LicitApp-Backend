package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleRequisitoDTO {
    private String indicador;
    private BigDecimal valorRequerido;
    private BigDecimal valorObtenido;
    private boolean cumple;
    /** Estado explícito del requisito: CUMPLE | NO_CUMPLE | REQUIERE_VERIFICACION. */
    private String estado;
    private String observacion;
}
