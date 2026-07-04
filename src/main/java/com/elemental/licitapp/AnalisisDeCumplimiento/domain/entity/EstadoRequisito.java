package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

/**
 * Estado de un {@link DetalleRequisito} tras evaluarlo. Hace explícito lo que antes
 * viajaba solo dentro del texto del mensaje ("REQUIERE VERIFICACIÓN"), para que el
 * frontend NO tenga que hacer string-matching sobre la observación.
 *
 * <ul>
 *   <li>{@link #CUMPLE} — el requisito se satisface (el flag {@code cumple} es true).</li>
 *   <li>{@link #NO_CUMPLE} — el requisito no se satisface con un valor numérico comparable.</li>
 *   <li>{@link #REQUIERE_VERIFICACION} — el indicador es indeterminado (denominador cero,
 *       dato faltante o un integrante indeterminado en la ruta plural): NO se puede afirmar
 *       cumplimiento ni declararlo un incumplimiento numérico; exige revisión manual. El flag
 *       {@code cumple} es false para no habilitar por defecto.</li>
 * </ul>
 */
public enum EstadoRequisito {
    CUMPLE,
    NO_CUMPLE,
    REQUIERE_VERIFICACION
}
