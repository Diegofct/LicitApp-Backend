package com.elemental.licitapp.Licitaciones.domain.entity;

import java.math.BigDecimal;

/**
 * Criterios de búsqueda del listado de obra pública. Van juntos en un objeto y no como
 * parámetros sueltos porque ya son seis y todos son opcionales.
 *
 * <p>El presupuesto viaja <b>en pesos</b> ({@code precio_base} del dataset), no en SMMLV: la
 * conversión la hace el frontend, que es donde ya vive el salario mínimo vigente. Así el valor
 * del SMMLV sigue definido en un solo lugar.
 *
 * @param entidad         coincidencia parcial e insensible a mayúsculas sobre el nombre
 * @param departamento    valor exacto tal como lo publica SECOP
 * @param presupuestoMin  cota inferior en pesos, inclusive
 * @param presupuestoMax  cota superior en pesos, inclusive
 * @param soloVigentes    excluye los procesos cuya fecha de cierre ya pasó
 * @param orden           criterio de ordenamiento
 */
public record FiltroLicitaciones(
        String entidad,
        String departamento,
        BigDecimal presupuestoMin,
        BigDecimal presupuestoMax,
        boolean soloVigentes,
        OrdenLicitaciones orden) {

    public FiltroLicitaciones {
        orden = orden != null ? orden : OrdenLicitaciones.PUBLICACION;
    }

    /** Filtro vacío: el listado completo, ordenado por publicación, como antes de los filtros. */
    public static FiltroLicitaciones vacio() {
        return new FiltroLicitaciones(null, null, null, null, false, OrdenLicitaciones.PUBLICACION);
    }

    /** Criterios de ordenamiento admitidos por el listado. */
    public enum OrdenLicitaciones {
        /** Lo más recién publicado primero. Es el orden histórico del módulo. */
        PUBLICACION,
        /** Lo que cierra antes primero: el orden útil para decidir a qué presentarse. */
        CIERRE
    }
}
