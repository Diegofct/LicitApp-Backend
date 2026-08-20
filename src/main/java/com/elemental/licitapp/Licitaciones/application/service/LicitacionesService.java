package com.elemental.licitapp.Licitaciones.application.service;

import com.elemental.licitapp.Licitaciones.application.ports.out.DocumentosSecopPort;
import com.elemental.licitapp.Licitaciones.application.ports.out.SecopApiPort;
import com.elemental.licitapp.Licitaciones.domain.entity.DocumentoProceso;
import com.elemental.licitapp.Licitaciones.domain.entity.EstadoProceso;
import com.elemental.licitapp.Licitaciones.domain.entity.FiltroLicitaciones;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class LicitacionesService {

    /**
     * Nombres con los que las entidades suelen titular el pliego. Al ser una heurística sobre
     * el nombre del archivo, solo se usa para ordenar y destacar: la lista completa de
     * documentos se devuelve siempre.
     */
    private static final List<String> PISTAS_DE_PLIEGO =
            List.of("PLIEGO", "DOCUMENTO BASE", "CONDICIONES");

    /**
     * Pistas para reconocer la matriz de indicadores. Verificado sobre 35 licitaciones de obra
     * recientes: detecta 44 documentos sin un solo falso positivo.
     */
    private static final List<String> PISTAS_DE_MATRIZ = List.of("FINANCIER", "MATRIZ");

    private final SecopApiPort secopApiPort;
    private final DocumentosSecopPort documentosSecopPort;

    public LicitacionesService(SecopApiPort secopApiPort, DocumentosSecopPort documentosSecopPort) {
        this.secopApiPort = secopApiPort;
        this.documentosSecopPort = documentosSecopPort;
    }

    public Page<Licitacion> obtenerLicitacionesObraPublica(Pageable pageable,
                                                           FiltroLicitaciones filtro) {
        return secopApiPort.obtenerLicitacionesObraPublica(pageable, filtro);
    }

    /** Departamentos con procesos, para el desplegable de filtro de la Búsqueda SECOP. */
    public List<String> obtenerDepartamentos() {
        return secopApiPort.obtenerDepartamentos();
    }

    /**
     * Documentos publicados en un proceso, con los que parecen ser el pliego de primeras y el
     * resto por fecha de carga descendente. Sin identificador de portafolio no hay nada que
     * consultar: se devuelve vacío en vez de fallar, porque los cuadros y procesos antiguos
     * pueden no tenerlo.
     */
    public List<DocumentoProceso> obtenerDocumentosDelProceso(String idDelPortafolio) {
        if (idDelPortafolio == null || idDelPortafolio.isBlank()) {
            return List.of();
        }

        List<DocumentoProceso> documentos = documentosSecopPort.obtenerDocumentos(idDelPortafolio);
        documentos.forEach(documento -> {
            documento.setEsPliego(pareceCategoriaPliego(documento));
            documento.setEsMatrizIndicadores(pareceMatrizDeIndicadores(documento));
        });

        Comparator<DocumentoProceso> destacadosPrimero = Comparator.comparing(
                documento -> documento.isEsPliego() || documento.isEsMatrizIndicadores(),
                Comparator.reverseOrder());
        Comparator<DocumentoProceso> masRecientePrimero =
                Comparator.comparing(DocumentoProceso::getFechaCarga,
                        Comparator.nullsLast(Comparator.reverseOrder()));

        return documentos.stream()
                .sorted(destacadosPrimero.thenComparing(masRecientePrimero))
                .toList();
    }

    /**
     * Documentos de un proceso identificado por su {@code idDelProceso} ({@code CO1.REQ.*}).
     * Se resuelve primero el portafolio, que es la llave real del dataset de archivos. Es la
     * vía que usa el Cuadro de Obra, que solo guarda el identificador del proceso.
     */
    public List<DocumentoProceso> obtenerDocumentosPorProceso(String idDelProceso) {
        return secopApiPort.resolverPortafolio(idDelProceso)
                .map(this::obtenerDocumentosDelProceso)
                .orElseGet(List::of);
    }

    /**
     * Fase y desenlace del proceso en SECOP II, incluida su URL. Se resuelve contra la API
     * porque nada de esto se guarda en base de datos; así funciona igual para los procesos
     * antiguos, que solo tienen guardado su identificador.
     */
    public Optional<EstadoProceso> obtenerEstadoDelProceso(String idDelProceso) {
        if (idDelProceso == null || idDelProceso.isBlank()) {
            return Optional.empty();
        }
        return secopApiPort.resolverEstadoProceso(idDelProceso);
    }

    private boolean pareceMatrizDeIndicadores(DocumentoProceso documento) {
        String nombre = documento.getNombre();
        if (nombre == null) {
            return false;
        }
        String enMayusculas = nombre.toUpperCase();
        return enMayusculas.contains("INDICADORES")
                && PISTAS_DE_MATRIZ.stream().anyMatch(enMayusculas::contains);
    }

    private boolean pareceCategoriaPliego(DocumentoProceso documento) {
        String nombre = documento.getNombre();
        if (nombre == null) {
            return false;
        }
        String enMayusculas = nombre.toUpperCase();
        return PISTAS_DE_PLIEGO.stream().anyMatch(enMayusculas::contains);
    }
}
