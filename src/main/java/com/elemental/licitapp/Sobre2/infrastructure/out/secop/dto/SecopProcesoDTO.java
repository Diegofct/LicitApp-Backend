package com.elemental.licitapp.Sobre2.infrastructure.out.secop.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Fila cruda del dataset "SECOP II - Procesos de Contratacion" (p6dx-8zbt), recortada a los
 * campos que hacen falta para resolver la identidad del proceso.
 *
 * <p>Es el mismo dataset que consulta el slice Licitaciones, pero alli se lee otra cosa: el
 * catalogo de procesos publicados. Aqui solo interesa {@code id_del_portafolio}, que
 * Licitaciones ni siquiera pide en su {@code $select}.
 */
@Data
public class SecopProcesoDTO {

    @JsonProperty("id_del_proceso")
    private String idDelProceso;

    /** El {@code CO1.BDOS.*}: la llave que cruza contra el dataset de ofertas. */
    @JsonProperty("id_del_portafolio")
    private String idDelPortafolio;

    @JsonProperty("nit_entidad")
    private String nitEntidad;

    @JsonProperty("referencia_del_proceso")
    private String referenciaDelProceso;

    @JsonProperty("precio_base")
    private BigDecimal precioBase;
}
