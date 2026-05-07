package com.elemental.licitapp.CuadroDeObra.domain.enums;

import java.util.Map;
import java.util.Set;

public enum CuadroDeObraEstado {
    POR_PRESENTAR,
    PRESENTADO,
    ADJUDICADO,
    NO_ADJUDICADO,
    CANCELADO;

    private static final Map<CuadroDeObraEstado, Set<CuadroDeObraEstado>> TRANSICIONES_PERMITIDAS = Map.of(
            POR_PRESENTAR, Set.of(PRESENTADO, CANCELADO),
            PRESENTADO,    Set.of(ADJUDICADO, NO_ADJUDICADO, CANCELADO),
            ADJUDICADO,    Set.of(),
            NO_ADJUDICADO, Set.of(),
            CANCELADO,     Set.of()
    );

    public boolean puedeTransicionarA(CuadroDeObraEstado destino) {
        return TRANSICIONES_PERMITIDAS.getOrDefault(this, Set.of()).contains(destino);
    }
}
