package com.elemental.licitapp.Licitaciones.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Fase y desenlace de un proceso en SECOP II. Es un modelo de solo lectura: no se persiste en
 * ninguna tabla, se resuelve contra la API cada vez que se consulta. Así el dato sirve igual
 * para procesos antiguos que nunca guardaron nada más que su identificador.
 */
@Data
@NoArgsConstructor
public class EstadoProceso {

    private String idDelProceso;

    /** Evento en el que va el proceso ("Fase de ofertas", "Presentación de observaciones"). */
    private String fase;

    /** Estado consolidado de SECOP; vale "Adjudicado" cuando el proceso ya se resolvió. */
    private String estadoResumen;

    private String estadoDelProcedimiento;

    private String url;

    private boolean adjudicado;

    /**
     * Cuántos proponentes se presentaron ({@code respuestas_al_procedimiento}). Solo queda
     * poblado una vez cierra la recepción de ofertas; antes de eso SECOP publica cero.
     */
    private Integer numeroDeOferentes;

    private Integer numeroDeLotes;

    /**
     * Última vez que la entidad publicó algo en el proceso. Si es posterior al análisis, hubo
     * adenda y conviene revisar el pliego de nuevo.
     */
    private LocalDate fechaUltimaPublicacion;

    /**
     * Proceso adjudicado por lotes, con más de un ganador. Cambia cómo hay que leer
     * {@link #adjudicaciones}: ver la nota de ese campo.
     */
    private boolean adjudicacionPorLotes;

    /**
     * Adjudicaciones del proceso. Es una lista y no un campo suelto porque los procesos por
     * lotes tienen un ganador por lote.
     *
     * <p><b>En los procesos por lotes el valor llega en {@code null}, a propósito.</b> Para
     * ellos SECOP publica el producto cruzado de ganadores por valores —un proceso de 18 lotes
     * trae 18 ganadores × 18 valores = 324 filas— y no hay ninguna columna que diga qué lote
     * ganó cada quién. Emparejarlos sería inventar el dato, así que solo se reportan los
     * ganadores; el desglose por lote hay que verlo en SECOP.
     */
    private List<Adjudicacion> adjudicaciones = new ArrayList<>();

    /**
     * Quién ganó, por cuánto y cuándo. El NIT no se incluye: SECOP lo publica inservible en
     * más de la mitad de los casos. El valor es {@code null} en adjudicaciones por lotes.
     */
    public record Adjudicacion(String proveedor, BigDecimal valor, LocalDate fecha) {}
}
