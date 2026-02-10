package com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller;

import com.elemental.licitapp.Licitaciones.application.service.LicitacionesService;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/licitaciones")
public class LicitacionesController {

    private final LicitacionesService licitacionesService;

    public LicitacionesController(LicitacionesService service) {
        this.licitacionesService = service;
    }

    @GetMapping("/publicas")
    public ResponseEntity<Page<Licitacion>> obtenerLicitacionesPublicas(Pageable pageable) {
        return ResponseEntity.ok(licitacionesService.obtenerLicitacionesPublicas(pageable));
    }

    @GetMapping("/obra-publica")
    public ResponseEntity<Page<Licitacion>> obtenerLicitacionesObraPublica(Pageable pageable) {
        return ResponseEntity.ok(licitacionesService.obtenerLicitacionesObraPublica(pageable));
    }
}
