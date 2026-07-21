package com.elemental.licitapp.CuadroDeObra.domain.enums;

/**
 * Marca "nos presentamos" de un Cuadro de Obra. La ausencia de marca se representa con
 * {@code null} en la entidad (no hay valor intermedio en el enum), de modo que la marca
 * cicla sin-marca -> SI -> NO -> sin-marca.
 */
public enum PresentacionMarca {
    SI,
    NO
}
