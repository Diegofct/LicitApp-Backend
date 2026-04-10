package com.elemental.licitapp.AnalisisDeCumplimiento.domain.model;

import java.math.BigDecimal;

public record DetalleRequisito(
    String nombre,
    BigDecimal valorRequerido,
    BigDecimal valorActual,
    boolean cumple,
    String mensaje
) {}
