package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrantePropuestoDTO {
    private Long empresaId;
    private String nit;
    private String razonSocial;
    private BigDecimal porcentaje;
    private boolean solicitante;
    private List<String> requisitosQueCubre;
}
