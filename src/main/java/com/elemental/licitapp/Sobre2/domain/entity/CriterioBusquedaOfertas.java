package com.elemental.licitapp.Sobre2.domain.entity;

/**
 * Con que se identifica el proceso frente al dataset de ofertas. Son dos caminos
 * excluyentes, y el segundo es una degradacion del primero, no un filtro adicional.
 *
 * <p>Sellado a proposito: el {@code switch} que construye la clausula {@code $where} queda
 * exhaustivo, asi que un tercer criterio futuro rompe la compilacion en vez de colarse
 * silenciosamente por la rama equivocada.
 */
public sealed interface CriterioBusquedaOfertas {

    /**
     * Camino preferente: identidad exacta. Un {@code CO1.BDOS.*} corresponde a un unico
     * proceso, asi que no hace falta acotar por entidad.
     */
    record PorIdDeProcesoDeCompra(String idDelPortafolio) implements CriterioBusquedaOfertas {
    }

    /**
     * Camino degradado, para cuadros que no existen en SECOP (precuadros manuales) o cuyo
     * proceso no se pudo resolver. Es aproximado: la referencia no es unica entre entidades.
     */
    record PorReferencia(String referenciaProceso, String nitEntidad) implements CriterioBusquedaOfertas {
    }
}
