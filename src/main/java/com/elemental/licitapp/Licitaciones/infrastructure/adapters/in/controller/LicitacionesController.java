package com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller;

import com.elemental.licitapp.Licitaciones.application.service.LicitacionesService;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
