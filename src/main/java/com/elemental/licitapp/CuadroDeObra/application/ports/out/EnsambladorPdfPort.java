package com.elemental.licitapp.CuadroDeObra.application.ports.out;

import java.util.List;

/**
 * Frontera con la libreria de manipulacion de PDF (PDFBox queda detras del adaptador).
 * Permite contar las paginas de un pliego y ensamblar un PDF nuevo con solo las paginas
 * seleccionadas por el analista, para no enviar el pliego completo al modelo (control de costo).
 */
public interface EnsambladorPdfPort {

    /**
     * @return numero total de paginas del PDF.
     * @throws com.elemental.licitapp.Exception.PliegoIlegibleException si el PDF no se puede leer
     */
    int contarPaginas(byte[] pdf);

    /**
     * Ensambla un PDF nuevo que contiene unicamente las paginas indicadas, en ese orden.
     *
     * @param pdf            PDF original
     * @param paginas1Based  paginas a incluir (1-based), ya validadas dentro del rango
     * @throws com.elemental.licitapp.Exception.PliegoIlegibleException si el PDF no se puede leer
     */
    byte[] ensamblarPaginas(byte[] pdf, List<Integer> paginas1Based);
}
