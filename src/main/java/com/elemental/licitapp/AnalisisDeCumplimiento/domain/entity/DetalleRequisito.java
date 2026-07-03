package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import java.math.BigDecimal;

public record DetalleRequisito(
    TipoRequisito tipo,
    BigDecimal valorRequerido,
    BigDecimal valorActual,
    boolean cumple,
    EstadoRequisito estado,
    String mensaje
) {
    /**
     * Constructor de conveniencia para los casos numéricos normales: el estado se deriva
     * del flag {@code cumple} (CUMPLE / NO_CUMPLE). Los indeterminados que requieren revisión
     * manual usan el constructor canónico pasando {@link EstadoRequisito#REQUIERE_VERIFICACION}.
     */
    public DetalleRequisito(TipoRequisito tipo, BigDecimal valorRequerido, BigDecimal valorActual,
                            boolean cumple, String mensaje) {
        this(tipo, valorRequerido, valorActual, cumple,
            cumple ? EstadoRequisito.CUMPLE : EstadoRequisito.NO_CUMPLE, mensaje);
    }

    /** Etiqueta legible del requisito para presentación (frontend); deriva del tipo. */
    public String nombre() {
        return tipo.etiqueta();
    }
}
