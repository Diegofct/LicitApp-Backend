package com.elemental.licitapp.Sobre2.domain.entity;

import java.math.BigDecimal;

/**
 * Datos del proceso que el slice Sobre 2 necesita de CuadroDeObra para poder trabajar: con
 * que se busca en SECOP II y el presupuesto oficial contra el cual se calcula el porcentaje.
 *
 * @param idDelProceso       {@code CO1.REQ.*} de SECOP II. Es la via preferente para buscar
 *                           las ofertas, porque identifica el proceso sin ambiguedad. Queda
 *                           NULL en los cuadros cargados a mano, que no existen en SECOP
 * @param numeroProceso      referencia del proceso. Solo se usa como via degradada: no es
 *                           unica entre entidades
 * @param presupuestoOficial preferimos el capturado del pliego definitivo
 *                           ({@code RequisitoLicitacion.presupuesto}) sobre el monto que
 *                           trae SECOP, porque el segundo corresponde al proyecto de pliego
 *                           y puede haber cambiado por adenda
 */
public record DatosProceso(
        Long cuadroDeObraId,
        String idDelProceso,
        String numeroProceso,
        String entidadContratante,
        BigDecimal presupuestoOficial
) {
}
