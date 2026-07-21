package com.elemental.licitapp.Seguridad.domain.enums;

/**
 * Roles del sistema. Se persisten como texto (nombre del enum) y se exponen a
 * Spring Security con el prefijo ROLE_ (ej. ROLE_ANALISTA) al construir las
 * authorities en el filtro de autenticacion.
 *
 * - ANALISTA: la licitadora. Opera todo el flujo (procesos, cuadro de obra,
 *             analisis de cumplimiento, consorcios, seguimiento) y gestiona proponentes.
 * - ADMIN:    gestion de usuarios + todo lo del analista.
 */
public enum Rol {
    ANALISTA,
    ADMIN
}
