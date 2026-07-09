package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

/**
 * Respuesta de GET /cuadro-de-obra/refs. Expone el {@code numeroProceso}
 * (cruce con {@code Licitacion.numero} del módulo Licitaciones) y el {@code id}
 * del cuadro ya guardado, para que el frontend evite duplicados y precargue el
 * registro existente.
 */
public record CuadroDeObraRefDTO(Long id, String numeroProceso) {
}
