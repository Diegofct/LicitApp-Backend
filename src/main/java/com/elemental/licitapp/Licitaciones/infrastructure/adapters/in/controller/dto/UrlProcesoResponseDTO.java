package com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller.dto;

/**
 * Enlace al proceso en SECOP II. La {@code url} llega en {@code null} cuando SECOP no la
 * publica: que falte el enlace no es un error, quien consume simplemente no lo muestra.
 */
public record UrlProcesoResponseDTO(String idDelProceso, String url) {
}
