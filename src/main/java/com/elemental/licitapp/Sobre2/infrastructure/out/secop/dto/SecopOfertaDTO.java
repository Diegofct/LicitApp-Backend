package com.elemental.licitapp.Sobre2.infrastructure.out.secop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Fila cruda del dataset "SECOPII - Ofertas Por Proceso" (wi7w-2nvm y su historico
 * b28v-edj8, mismo esquema).
 *
 * <p>Ojo: una misma oferta genera N filas, una por integrante del consorcio o union
 * temporal. Solo cambia {@code c_digo_proveedor}; el resto es identico. La deduplicacion
 * va por {@code identificador_de_la_oferta}.
 */
@Data
public class SecopOfertaDTO {

    @JsonProperty("identificador_de_la_oferta")
    private String identificadorDeLaOferta;

    @JsonProperty("referencia_de_la_oferta")
    private String referenciaDeLaOferta;

    @JsonProperty("referencia_del_proceso")
    private String referenciaDelProceso;

    @JsonProperty("id_del_proceso_de_compra")
    private String idDelProcesoDeCompra;

    @JsonProperty("valor_de_la_oferta")
    private BigDecimal valorDeLaOferta;

    @JsonProperty("nombre_proveedor")
    private String nombreProveedor;

    @JsonProperty("nit_del_proveedor")
    private String nitDelProveedor;

    @JsonProperty("entidad_compradora")
    private String entidadCompradora;

    @JsonProperty("nit_entidad_compradora")
    private String nitEntidadCompradora;

    @JsonProperty("moneda")
    private String moneda;

    @JsonProperty("modalidad")
    private String modalidad;

    /** calendar_date de Socrata: llega como "2025-03-14T00:00:00.000", se parsea en el mapper. */
    @JsonProperty("fecha_de_registro")
    private String fechaDeRegistro;
}
