package com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller;

import com.elemental.licitapp.Licitaciones.application.service.LicitacionesService;
import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/licitaciones")
public class LicitacionesController {

    private final LicitacionesService licitacionesService;

    public LicitacionesController(LicitacionesService service) {
        this.licitacionesService = service;
    }

    @GetMapping("/publicas")
    public ResponseEntity<List<Licitacion>> obtenerLicitacionesPublicas(@RequestParam(defaultValue = "1") int page,
                                                                        @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(licitacionesService.obtenerLicitacionesPublicas(page, size));
    }

    @GetMapping("/obra-publica")
    public ResponseEntity<List<Licitacion>> obtenerLicitacionesObraPublica(@RequestParam(defaultValue = "1") int page,
                                                                             @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(licitacionesService.obtenerLicitacionesObraPublica(page, size));
    }
}