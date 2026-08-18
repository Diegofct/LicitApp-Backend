package com.elemental.licitapp.Sobre2.domain.enums;

/**
 * Regimen normativo bajo el cual la entidad pondera la oferta economica.
 *
 * <p>Coexisten dos: los Documentos Tipo (obligatorios para licitacion publica de obra de
 * infraestructura de transporte, art. 2.2.1.2.6.1.4 del Decreto 1082/2015) y el esquema
 * original del Decreto 1082, que siguen usando las entidades no sujetas a Documentos Tipo.
 * Los metodos NO son los mismos: el vigente arranca con una mediana estadistica real.
 */
public enum RegimenPonderacion {

    /** Documentos Tipo de obra publica, numeral 4.1.4. Es el regimen por defecto. */
    DOCUMENTOS_TIPO,

    /** Decreto 1082 de 2015, art. 2.2.1.1.2.2.2 / pliego de condiciones tipo v2 (2015). */
    DECRETO_1082,

    /** Metodo presente en ambos regimenes (menor valor). */
    AMBOS
}
