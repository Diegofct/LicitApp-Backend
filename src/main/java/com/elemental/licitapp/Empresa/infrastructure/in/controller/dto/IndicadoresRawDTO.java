package com.elemental.licitapp.Empresa.infrastructure.in.controller.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class IndicadoresRawDTO {
    private BigDecimal activoCorriente;
    private BigDecimal pasivoCorriente;
    private BigDecimal activoTotal;
    private BigDecimal pasivoTotal;
    private BigDecimal utilidadOperacional;
    private BigDecimal gastosInteres;
    private BigDecimal patrimonio;
}
