package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Borrador editable que devuelve POST /cuadro-de-obra/{id}/extraer-pliego. El front lo
 * muestra en el modal de requisitos, el analista lo revisa/corrige y, al confirmar, usa los
 * endpoints existentes POST /{id}/requisitos (crear) o PATCH /{id}/requisitos (parcial).
 * Reutiliza {@link RequisitoLicitacionRequestDTO} para que el mapeo al confirmar sea 1:1.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExtraccionPliegoResponseDTO {

    /** Requisitos habilitantes extraidos (campos nulos = no encontrados). */
    private RequisitoLicitacionRequestDTO requisitos;

    /** Avisos de la validacion (campos nulos, porcentajes normalizados, valores fuera de rango). */
    private List<String> advertencias;
}
