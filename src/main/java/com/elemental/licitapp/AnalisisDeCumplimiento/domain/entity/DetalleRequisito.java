package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import java.math.BigDecimal;

public record DetalleRequisito(
    String nombre,
    BigDecimal valorRequerido,
    BigDecimal valorActual,
    boolean cumple,
    String mensaje
) {}
