package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto;

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
    private String observacion;
}
