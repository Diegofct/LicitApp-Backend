package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

/**
 * Respuesta de GET /cuadro-de-obra/refs. Expone el {@code idDelProceso}
 * (cruce con {@code Licitacion.idDelProceso} del modulo Licitaciones) y el {@code id}
 * del cuadro ya guardado, para que el frontend evite duplicados y precargue el
 * registro existente. El {@code numeroProceso} se mantiene por ser el dato legible
 * del proceso, pero no sirve como llave: se repite entre entidades.
 */
public record CuadroDeObraRefDTO(Long id, String numeroProceso, String idDelProceso) {
}
