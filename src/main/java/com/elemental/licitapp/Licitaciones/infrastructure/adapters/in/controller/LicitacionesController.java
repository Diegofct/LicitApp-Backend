package com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller;

import com.elemental.licitapp.Licitaciones.application.service.LicitacionesService;
import com.elemental.licitapp.Licitaciones.domain.entity.DocumentoProceso;
import com.elemental.licitapp.Licitaciones.domain.entity.EstadoProceso;
import com.elemental.licitapp.Licitaciones.domain.entity.FiltroLicitaciones;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/licitaciones")
public class LicitacionesController {

    private static final int MAX_PAGE_SIZE = 1000;

    private final LicitacionesService licitacionesService;

    public LicitacionesController(LicitacionesService service) {
        this.licitacionesService = service;
    }

    /**
     * Listado de obra pública. Todos los filtros son opcionales y se aplican en la propia API
     * de SECOP, no en memoria: sin ninguno el resultado es el mismo de siempre.
     *
     * <p>El presupuesto se recibe <b>en pesos</b> y no en SMMLV, para no duplicar aquí el valor
     * del salario mínimo que el frontend ya conoce.
     */
    @GetMapping("/obra-publica")
    public ResponseEntity<Page<Licitacion>> obtenerLicitacionesObraPublica(
            @PageableDefault(size = 10) Pageable pageable,
            @RequestParam(required = false) String entidad,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) BigDecimal presupuestoMin,
            @RequestParam(required = false) BigDecimal presupuestoMax,
            @RequestParam(defaultValue = "false") boolean soloVigentes,
            @RequestParam(defaultValue = "PUBLICACION") FiltroLicitaciones.OrdenLicitaciones orden) {
        if (pageable.getPageSize() > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException(
                    "El tamaño de página no puede exceder " + MAX_PAGE_SIZE + " (límite de la API SECOP).");
        }
        FiltroLicitaciones filtro = new FiltroLicitaciones(
                entidad, departamento, presupuestoMin, presupuestoMax, soloVigentes, orden);
        return ResponseEntity.ok(licitacionesService.obtenerLicitacionesObraPublica(pageable, filtro));
    }

    /**
     * Departamentos con procesos, para el desplegable de filtro. Se devuelven tal como los
     * escribe SECOP, que es lo que hace que el valor elegido vuelva a emparejar.
     */
    @GetMapping("/departamentos")
    public ResponseEntity<List<String>> obtenerDepartamentos() {
        return ResponseEntity.ok(licitacionesService.obtenerDepartamentos());
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
     * Fase y desenlace del proceso en SECOP II: en qué evento va, si ya se adjudicó, a quién,
     * por cuánto y contra cuántos oferentes. Incluye la URL del proceso, así que el seguimiento
     * resuelve el enlace y el estado en una sola llamada.
     *
     * <p>Responde 200 con cuerpo vacío cuando SECOP no conoce el identificador: que un proceso
     * no esté en el dataset no es un error, y así el frontend solo oculta el bloque.
     */
    @GetMapping("/estado-proceso")
    public ResponseEntity<EstadoProceso> obtenerEstadoDelProceso(
            @RequestParam String idDelProceso) {
        return ResponseEntity.ok(licitacionesService.obtenerEstadoDelProceso(idDelProceso).orElse(null));
    }
}
