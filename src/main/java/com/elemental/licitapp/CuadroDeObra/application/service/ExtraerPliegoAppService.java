package com.elemental.licitapp.CuadroDeObra.application.service;

import com.elemental.licitapp.CuadroDeObra.application.ports.in.ExtraerPliegoUseCase;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.CuadroDeObraRepositoryPort;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.EnsambladorPdfPort;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.ExtraerPliegoPort;
import com.elemental.licitapp.Exception.PliegoIlegibleException;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccionPliego;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Orquesta la extraccion de requisitos del pliego desde CuadroDeObra: valida que el cuadro
 * exista, parsea la lista de paginas que indico el analista, recorta el PDF a esas paginas
 * (control de costo) y delega la extraccion en el slice InteligenciaArtificial. No persiste.
 */
@Service
public class ExtraerPliegoAppService implements ExtraerPliegoUseCase {

    private final CuadroDeObraRepositoryPort cuadroRepository;
    private final EnsambladorPdfPort ensamblador;
    private final ExtraerPliegoPort extraerPliegoPort;
    private final int maxPaginas;

    public ExtraerPliegoAppService(CuadroDeObraRepositoryPort cuadroRepository,
                                   EnsambladorPdfPort ensamblador,
                                   ExtraerPliegoPort extraerPliegoPort,
                                   @Value("${licitapp.ia.max-paginas-pliego:50}") int maxPaginas) {
        this.cuadroRepository = cuadroRepository;
        this.ensamblador = ensamblador;
        this.extraerPliegoPort = extraerPliegoPort;
        this.maxPaginas = maxPaginas;
    }

    @Override
    public ResultadoExtraccionPliego extraerRequisitos(Long cuadroId, byte[] pdf, String paginas, String nombreArchivo) {
        // 1. El cuadro debe existir (404 si no).
        cuadroRepository.findById(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuadro de obra con id: " + cuadroId + " no encontrado"));

        if (pdf == null || pdf.length == 0) {
            throw new PliegoIlegibleException("El archivo del pliego esta vacio.");
        }

        // 2. Parsear la lista de paginas/rangos.
        List<Integer> seleccion = parsearPaginas(paginas);

        // 3. Contar paginas (tambien valida que sea un PDF legible) y validar el rango/tope.
        int total = ensamblador.contarPaginas(pdf);
        validarSeleccion(seleccion, total);

        // 4. Ensamblar un PDF solo con esas paginas y delegar en IA.
        byte[] ensamblado = ensamblador.ensamblarPaginas(pdf, seleccion);
        return extraerPliegoPort.extraer(ensamblado, nombreArchivo);
    }

    /**
     * Parsea "20-22,50,116-118" a una lista 1-based, en orden de aparicion y sin duplicados.
     */
    private List<Integer> parsearPaginas(String paginas) {
        if (paginas == null || paginas.isBlank()) {
            throw new PliegoIlegibleException("Debe indicar las paginas a extraer (ej. \"20-22,50,116-118\").");
        }
        Set<Integer> resultado = new LinkedHashSet<>();
        for (String token : paginas.split(",")) {
            String t = token.trim();
            if (t.isEmpty()) {
                continue;
            }
            try {
                if (t.contains("-")) {
                    String[] limites = t.split("-", 2);
                    int inicio = Integer.parseInt(limites[0].trim());
                    int fin = Integer.parseInt(limites[1].trim());
                    if (inicio < 1 || fin < inicio) {
                        throw new PliegoIlegibleException("Rango de paginas invalido: \"" + t + "\".");
                    }
                    for (int p = inicio; p <= fin; p++) {
                        resultado.add(p);
                    }
                } else {
                    int p = Integer.parseInt(t);
                    if (p < 1) {
                        throw new PliegoIlegibleException("Numero de pagina invalido: \"" + t + "\".");
                    }
                    resultado.add(p);
                }
            } catch (NumberFormatException ex) {
                throw new PliegoIlegibleException("Formato de paginas invalido en \"" + t + "\"; use por ejemplo \"20-22,50,116-118\".");
            }
        }
        if (resultado.isEmpty()) {
            throw new PliegoIlegibleException("No se indico ninguna pagina valida.");
        }
        return new ArrayList<>(resultado);
    }

    private void validarSeleccion(List<Integer> seleccion, int total) {
        for (Integer p : seleccion) {
            if (p > total) {
                throw new PliegoIlegibleException(
                        "La pagina " + p + " esta fuera del pliego (tiene " + total + " paginas).");
            }
        }
        if (seleccion.size() > maxPaginas) {
            throw new PliegoIlegibleException(
                    "Selecciono " + seleccion.size() + " paginas; el maximo permitido es " + maxPaginas
                            + ". Acote la seleccion a las paginas con requisitos habilitantes.");
        }
    }
}
