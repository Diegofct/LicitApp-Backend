package com.elemental.licitapp.Sobre2.application.service;

import com.elemental.licitapp.Sobre2.domain.entity.MomentoSobre2;

import java.util.List;

/**
 * Textos de las advertencias derivadas del momento del proceso. Viven aparte porque los
 * emiten dos casos de uso distintos (importar y analizar) y deben decir exactamente lo
 * mismo: el analista no tiene por que leer dos redacciones de la misma alerta.
 */
final class AvisosMomentoSobre2 {

    static final String SIN_INFORME_DEFINITIVO =
            "El proceso aun no registra informe de evaluacion definitivo en el seguimiento, asi que "
                    + "todavia no se sabe que oferentes quedaron habiles. Este analisis es exploratorio: "
                    + "sirve para estudiar a la competencia, no para fijar el valor con el que te presentas.";

    static final String SOBRE_2_SIN_ABRIR =
            "El Sobre 2 aun no se ha abierto en audiencia, por lo que los valores de los competidores "
                    + "pueden ser provisionales: mientras la oferta economica sigue cerrada, SECOP suele "
                    + "publicar el presupuesto oficial como marcador de posicion. Con esos datos las "
                    + "formulas de ponderacion dan cifras enganosas.";

    private AvisosMomentoSobre2() {
    }

    /** Agrega el aviso solo si no hay ya uno equivalente en la lista. */
    static void agregarSiFalta(List<String> advertencias, String aviso) {
        if (!advertencias.contains(aviso)) {
            advertencias.add(aviso);
        }
    }

    /** Advertencias que corresponden al momento del proceso, en el orden en que se leen. */
    static void agregarSegun(List<String> advertencias, MomentoSobre2 momento) {
        if (!momento.listoParaDecidir()) {
            agregarSiFalta(advertencias, SIN_INFORME_DEFINITIVO);
        }
        if (!momento.valoresDefinitivos()) {
            agregarSiFalta(advertencias, SOBRE_2_SIN_ABRIR);
        }
    }
}
