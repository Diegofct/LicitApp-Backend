package com.elemental.licitapp.Sobre2.application.ports.out;

import com.elemental.licitapp.Sobre2.domain.entity.ProcesoSecop;

import java.util.Optional;

/**
 * Frontera con el dataset de procesos de SECOP II (p6dx-8zbt). El slice lo usa solo para
 * resolver identidad: traducir el {@code CO1.REQ.*} del cuadro al {@code CO1.BDOS.*} con el
 * que se buscan las ofertas.
 */
public interface ProcesoSecopPort {

    /**
     * @return vacio si SECOP respondio pero no hay fila para ese id (proceso ajeno al
     *         dataset, id mal formado o publicacion incompleta). Un fallo de transporte
     *         lanza {@link com.elemental.licitapp.Exception.SecopApiException}, porque no es
     *         lo mismo "no existe" que "no pude preguntar"
     */
    Optional<ProcesoSecop> resolver(String idDelProceso);

    /**
     * Cuantos proponentes radicaron oferta al proceso.
     *
     * <p>Es el <b>unico</b> dato de la competencia que SECOP publica antes de abrir el
     * Sobre 2: hasta la audiencia se sabe cuantos son, pero no con cuanto vienen. Permite
     * distinguir "nadie se presento" de "hay 23 pero sus precios siguen sellados", que para
     * el licitador son situaciones opuestas.
     *
     * <p>Consulta aparte y no parte de {@link #resolver}, porque el conteo vive repartido
     * entre las filas por fase del proceso y solo se puede leer buscando por portafolio.
     * Se invoca unicamente cuando no llegaron ofertas, para no pagarla en el caso normal.
     *
     * @return vacio si SECOP no publica el dato
     */
    Optional<Integer> contarProponentes(String idDelPortafolio);
}
