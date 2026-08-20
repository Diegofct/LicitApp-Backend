package com.elemental.licitapp.Licitaciones.infrastructure.adapters.out.secop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class SecopLicitacionDTO {

    @JsonProperty("id_del_proceso")
    private String idDelProceso;

    @JsonProperty("entidad")
    private String entidad;

    @JsonProperty("descripci_n_del_procedimiento")
    private String objeto;

    @JsonProperty("precio_base")
    private BigDecimal cuantia;

    @JsonProperty("modalidad_de_contratacion")
    private String modalidad;

    @JsonProperty("subtipo_de_contrato")
    private String subtipoDeContrato;

    @JsonProperty("tipo_de_contrato")
    private String tipoDeProceso;

    @JsonProperty("referencia_del_proceso")
    private String numero;

    @JsonProperty("estado_del_procedimiento")
    private String estado;

    @JsonProperty("fecha_publicacion_consolidada")
    private LocalDateTime fechaPublicacionConsolidada;

    @JsonProperty("departamento_entidad")
    private String departamentoEntidad;

    @JsonProperty("ciudad_entidad")
    private String ciudadEntidad;

    @JsonProperty("urlproceso")
    private JsonNode urlProceso;

    @JsonProperty("codigo_principal_de_categoria")
    private String codigoUnpspc;

    @JsonProperty("fecha_de_recepcion_de")
    private LocalDateTime fechaRecepcionOfertas;

    /** SECOP la entrega como texto y a veces vale "0"; se normaliza en el mapper. */
    @JsonProperty("duracion")
    private String duracion;

    @JsonProperty("unidad_de_duracion")
    private String unidadDuracion;

    @JsonProperty("id_del_portafolio")
    private String idDelPortafolio;

    // --- Fase y desenlace del proceso (endpoint /estado-proceso) ---

    @JsonProperty("fase")
    private String fase;

    @JsonProperty("estado_resumen")
    private String estadoResumen;

    /** SECOP lo entrega como "Si"/"No", no como booleano. */
    @JsonProperty("adjudicado")
    private String adjudicado;

    /** Cuántos proponentes se presentaron. Vale 0 mientras la recepción sigue abierta. */
    @JsonProperty("respuestas_al_procedimiento")
    private Integer respuestasAlProcedimiento;

    @JsonProperty("numero_de_lotes")
    private Integer numeroDeLotes;

    @JsonProperty("fecha_de_ultima_publicaci")
    private LocalDateTime fechaUltimaPublicacion;

    @JsonProperty("nombre_del_proveedor")
    private String nombreDelProveedor;

    @JsonProperty("valor_total_adjudicacion")
    private BigDecimal valorTotalAdjudicacion;

    @JsonProperty("fecha_adjudicacion")
    private LocalDateTime fechaAdjudicacion;

}
