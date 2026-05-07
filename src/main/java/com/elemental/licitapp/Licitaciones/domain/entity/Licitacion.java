package com.elemental.licitapp.Licitaciones.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class Licitacion {

    private String idDelProceso;
    private String entidad;
    private String objeto;
    private BigDecimal cuantia;
    private String modalidad;
    private String numero;
    private String estado;
    private LocalDateTime fechaPublicacion;
    private String ubicacion;
    private String urlSecop;
    private String codigoUnpspc;
}
