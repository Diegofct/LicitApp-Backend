package com.elemental.licitapp.Licitaciones.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Un archivo publicado por la entidad dentro de un proceso de SECOP II (pliego, estudios
 * previos, anexos, formatos). Se lee del dataset de archivos de descarga y no se persiste.
 *
 * <p>La URL apunta directamente al repositorio de SECOP. Se entrega tal cual para que la
 * abra el navegador del analista: {@code community.secop.gov.co} rechaza con 403 las
 * peticiones sin un {@code User-Agent} de navegador, así que descargar el archivo desde el
 * backend exigiría falsear ese encabezado sin ganar nada a cambio.
 */
@Data
@NoArgsConstructor
public class DocumentoProceso {

    private String idDocumento;
    private String nombre;
    private String extension;
    private String descripcion;
    private Long tamanoBytes;
    private LocalDate fechaCarga;
    private String url;

    /**
     * Marca los documentos que parecen ser el pliego de condiciones, para mostrarlos de
     * primeras. Es una heurística sobre el nombre del archivo: ayuda a ordenar, no garantiza
     * nada, porque cada entidad nombra sus documentos como quiere.
     */
    private boolean esPliego;

    /**
     * Marca la "Matriz 2 - Indicadores financieros y organizacionales" y sus variantes. Es el
     * documento donde los pliegos tipo fijan los valores exigidos de liquidez, endeudamiento,
     * cobertura de intereses y rentabilidades: el Documento Base solo trae las fórmulas y
     * remite a esta matriz.
     */
    private boolean esMatrizIndicadores;
}
