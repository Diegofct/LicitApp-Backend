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

}