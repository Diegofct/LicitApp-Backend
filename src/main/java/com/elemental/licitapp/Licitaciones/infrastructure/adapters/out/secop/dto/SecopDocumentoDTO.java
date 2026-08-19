package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public class SecopDocumentoDTO {

    @JsonProperty("id_documento")
    private String idDocumento;

    /** Identificador de portafolio del proceso ({@code CO1.BDOS.*}); es la llave del cruce. */
    @JsonProperty("proceso")
    private String proceso;

    @JsonProperty("n_mero_de_contrato")
    private String numeroDeContrato;

    @JsonProperty("nombre_archivo")
    private String nombreArchivo;

    @JsonProperty("extensi_n")
    private String extension;

    @JsonProperty("descripci_n")
    private String descripcion;

    /** Tamaño en bytes; el dataset lo entrega como texto. */
    @JsonProperty("tamanno_archivo")
    private String tamanoArchivo;

    /** Fecha de tipo {@code calendar_date}, que Socrata entrega como "2026-01-15T00:00:00.000". */
    @JsonProperty("fecha_carga")
    private String fechaCarga;

    @JsonProperty("entidad")
    private String entidad;

    @JsonProperty("nit_entidad")
    private String nitEntidad;

    /** Igual que {@code urlproceso}: unas veces objeto {url, description} y otras texto plano. */
    @JsonProperty("url_descarga_documento")
    private JsonNode urlDescargaDocumento;
}
