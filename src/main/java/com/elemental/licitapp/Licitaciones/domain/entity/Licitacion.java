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

    /**
     * Fecha límite de recepción de ofertas publicada por SECOP. El dataset solo entrega el
     * día (siempre a las 00:00), nunca la hora real de cierre: sirve para precargar, no para
     * dar por confirmado el vencimiento.
     */
    private LocalDateTime fechaCierre;

    /**
     * Plazo de ejecución normalizado a meses a partir de {@code duracion} +
     * {@code unidad_de_duracion}. Es {@code null} cuando el dato de SECOP no es utilizable
     * (duración en cero o en una unidad que no se puede convertir).
     */
    private Integer plazoMeses;

    /**
     * Identificador del portafolio ({@code CO1.BDOS.*}). Es la llave con la que se cruzan los
     * documentos del proceso y las ofertas, no el {@code idDelProceso}.
     */
    private String idDelPortafolio;

    /**
     * Evento en el que va el proceso segun SECOP ("Fase de ofertas", "Presentacion de
     * observaciones"). Viene con el listado para no tener que consultarlo fila por fila.
     */
    private String fase;
}
