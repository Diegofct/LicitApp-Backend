package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.mapper;

import com.elemental.licitapp.Licitaciones.domain.entity.DocumentoProceso;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto.SecopDocumentoDTO;
import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Traduce una fila del dataset de archivos de descarga al modelo del slice. Clase utilitaria
 * con métodos estáticos, como el resto de mappers del proyecto.
 */
public final class SecopDocumentoMapper {

    private SecopDocumentoMapper() {
    }

    public static DocumentoProceso toDomain(SecopDocumentoDTO dto) {
        if (dto == null) {
            return null;
        }

        DocumentoProceso documento = new DocumentoProceso();
        documento.setIdDocumento(dto.getIdDocumento());
        documento.setNombre(dto.getNombreArchivo());
        documento.setExtension(dto.getExtension());
        documento.setDescripcion(dto.getDescripcion());
        documento.setTamanoBytes(aLong(dto.getTamanoArchivo()));
        documento.setFechaCarga(aFecha(dto.getFechaCarga()));
        documento.setUrl(extractUrl(dto.getUrlDescargaDocumento()));
        return documento;
    }

    private static Long aLong(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** Socrata entrega "2026-01-15T00:00:00.000"; solo interesa el día. */
    private static LocalDate aFecha(String valor) {
        if (valor == null || valor.length() < 10) {
            return null;
        }
        try {
            return LocalDate.parse(valor.substring(0, 10));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static String extractUrl(JsonNode url) {
        if (url == null || url.isNull()) {
            return null;
        }
        if (url.isTextual()) {
            return url.asText();
        }
        JsonNode urlField = url.get("url");
        return urlField != null && !urlField.isNull() ? urlField.asText() : null;
    }
}
