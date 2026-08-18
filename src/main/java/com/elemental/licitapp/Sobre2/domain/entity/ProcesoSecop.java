package com.elemental.licitapp.Sobre2.domain.entity;

import java.math.BigDecimal;

/**
 * Identidad de un proceso segun el dataset de procesos de SECOP II (p6dx-8zbt). Sirve para
 * una sola cosa: traducir el identificador que guarda el cuadro al identificador con el que
 * hay que buscar las ofertas.
 *
 * @param idDelProceso    {@code CO1.REQ.*}, el que guarda {@code CuadroDeObra.idDelProceso}
 * @param idDelPortafolio {@code CO1.BDOS.*}, el que usa el dataset de ofertas en
 *                        {@code id_del_proceso_de_compra}. Puede venir vacio en procesos
 *                        antiguos o mal publicados
 * @param nitEntidad      NIT de la entidad compradora, ya reducido a digitos
 * @param precioBase      presupuesto que SECOP publica con el proyecto de pliego. No es el
 *                        presupuesto oficial vigente si hubo adenda, por eso no sustituye al
 *                        que captura {@code RequisitoLicitacion}
 */
public record ProcesoSecop(
        String idDelProceso,
        String idDelPortafolio,
        String nitEntidad,
        String referenciaProceso,
        BigDecimal precioBase
) {

    public boolean tienePortafolio() {
        return idDelPortafolio != null && !idDelPortafolio.isBlank();
    }
}
