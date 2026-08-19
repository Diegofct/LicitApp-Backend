package com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller;

import com.elemental.licitapp.Licitaciones.application.service.LicitacionesService;
import com.elemental.licitapp.Licitaciones.domain.entity.DocumentoProceso;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller.dto.UrlProcesoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/licitaciones")
public class LicitacionesController {

    private static final int MAX_PAGE_SIZE = 1000;

    private final LicitacionesService licitacionesService;

    public LicitacionesController(LicitacionesService service) {
        this.licitacionesService = service;
    }

    @GetMapping("/obra-publica")
    public ResponseEntity<Page<Licitacion>> obtenerLicitacionesObraPublica(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String entidad) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "El tamaño de página no puede exceder " + MAX_PAGE_SIZE + " (límite de la API SECOP).");
        }
        return ResponseEntity.ok(licitacionesService.obtenerLicitacionesObraPublica(pageable, entidad));
    }

    /**
     * Documentos publicados por la entidad en un proceso (pliego, matriz de indicadores,
     * estudios previos, anexos).
     *
     * <p>Admite cualquiera de los dos identificadores porque cada consumidor tiene uno distinto:
     * la Búsqueda SECOP conoce el {@code idDelPortafolio} y el Cuadro de Obra solo guarda el
     * {@code idDelProceso}. Ambos viajan como parámetro de consulta y no como variable de ruta:
     * su formato es {@code CO1.BDOS.10672541} y los puntos complican el emparejamiento de rutas.
     */
    @GetMapping("/documentos")
    public ResponseEntity<List<DocumentoProceso>> obtenerDocumentosDelProceso(
            @RequestParam(required = false) String idDelPortafolio,
            @RequestParam(required = false) String idDelProceso) {
        if (idDelPortafolio != null && !idDelPortafolio.isBlank()) {
            return ResponseEntity.ok(licitacionesService.obtenerDocumentosDelProceso(idDelPortafolio));
        }
        if (idDelProceso != null && !idDelProceso.isBlank()) {
            return ResponseEntity.ok(licitacionesService.obtenerDocumentosPorProceso(idDelProceso));
        }
        throw new IllegalArgumentException(
                "Debe indicar idDelPortafolio o idDelProceso para consultar los documentos.");
    }

    /**
     * Enlace al proceso en SECOP II, para poder abrirlo desde el seguimiento y ver en qué
     * evento va. Responde 200 aunque SECOP no publique la URL: en ese caso llega en {@code null}.
     */
    @GetMapping("/url-proceso")
    public ResponseEntity<UrlProcesoResponseDTO> obtenerUrlDelProceso(
            @RequestParam String idDelProceso) {
        String url = licitacionesService.obtenerUrlDelProceso(idDelProceso).orElse(null);
        return ResponseEntity.ok(new UrlProcesoResponseDTO(idDelProceso, url));
    }
}
