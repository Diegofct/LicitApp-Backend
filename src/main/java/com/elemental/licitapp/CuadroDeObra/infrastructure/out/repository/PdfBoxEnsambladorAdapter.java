package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.application.ports.out.EnsambladorPdfPort;
import com.elemental.licitapp.Exception.PliegoIlegibleException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Adaptador de PDFBox: unico punto del codigo que conoce la libreria de PDF. Cuenta paginas
 * y ensambla un PDF nuevo con solo las paginas seleccionadas, para enviar al modelo unicamente
 * lo relevante (control de costo de tokens).
 */
@Component
public class PdfBoxEnsambladorAdapter implements EnsambladorPdfPort {

    @Override
    public int contarPaginas(byte[] pdf) {
        try (PDDocument documento = Loader.loadPDF(pdf)) {
            return documento.getNumberOfPages();
        } catch (IOException ex) {
            throw new PliegoIlegibleException("No se pudo leer el PDF del pliego: el archivo no es un PDF valido o esta danado.");
        }
    }

    @Override
    public byte[] ensamblarPaginas(byte[] pdf, List<Integer> paginas1Based) {
        try (PDDocument origen = Loader.loadPDF(pdf);
             PDDocument ensamblado = new PDDocument();
             ByteArrayOutputStream salida = new ByteArrayOutputStream()) {

            for (Integer pagina : paginas1Based) {
                ensamblado.importPage(origen.getPage(pagina - 1));
            }
            ensamblado.save(salida);
            return salida.toByteArray();
        } catch (IOException ex) {
            throw new PliegoIlegibleException("No se pudo procesar el PDF del pliego: el archivo no es un PDF valido o esta danado.");
        }
    }
}
