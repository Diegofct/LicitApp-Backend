package com.elemental.licitapp.CuadroDeObra.domain.projection;

/**
 * Referencia liviana de un cuadro de obra: solo lo necesario para que el
 * frontend cruce las licitaciones de SECOP II (por {@code idDelProceso})
 * contra los cuadros ya guardados, resalte las filas agregadas y abra el
 * registro existente en modo solo lectura. Evita traer las columnas TEXT
 * de la entidad completa.
 *
 * <p>{@code idDelProceso} es NULL en los cuadros cargados a mano: no existen en
 * SECOP y por tanto no cruzan contra ninguna licitacion.
 */
public record CuadroDeObraRef(Long id, String numeroProceso, String idDelProceso) {
}
