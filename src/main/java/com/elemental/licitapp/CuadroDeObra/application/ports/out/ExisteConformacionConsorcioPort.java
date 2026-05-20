package com.elemental.licitapp.CuadroDeObra.application.ports.out;

/**
 * Puerto de salida hacia el modulo AnalisisDeCumplimiento. Permite validar si
 * un cuadro de obra ya tiene una conformacion del proponente registrada
 * (individual / consorcio / union temporal) antes de aceptar la transicion
 * a PRESENTADO.
 */
public interface ExisteConformacionConsorcioPort {
    boolean existePara(Long cuadroDeObraId);
}
