package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller;

import com.elemental.licitapp.CuadroDeObra.application.service.CuadroDeObraService;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("cuadro-de-obra")
public class CuadroDeObraController {

    private final CuadroDeObraService cuadroDeObraService;

    public CuadroDeObraController(CuadroDeObraService cuadroDeObraService) {
        this.cuadroDeObraService = cuadroDeObraService;
    }

    @GetMapping
    public ResponseEntity<Page<CuadroDeObra>> getCuadrosPorVista(@RequestParam(defaultValue = "por-presentar") String vista, Pageable pageable) {
        Page<CuadroDeObra> page = cuadroDeObraService.findCuadrosPorVistas(vista, pageable);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuadroDeObra> getCuadroById(@PathVariable Long id) {
        return ResponseEntity.ok(cuadroDeObraService.findCuadroById(id));
    }

    @PostMapping
    public ResponseEntity<CuadroDeObra> createCuadro(@RequestBody CuadroDeObra nuevoCuadro) {
        CuadroDeObra cuadroCreado = cuadroDeObraService.createCuadro(nuevoCuadro);
        return new ResponseEntity<>(cuadroCreado, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuadroDeObra> updateCuadro(@PathVariable Long id, @RequestBody CuadroDeObra cuadroConActualizaciones){
        return ResponseEntity.ok(cuadroDeObraService.updateCuadro(id, cuadroConActualizaciones));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CuadroDeObra> updateEstado(@PathVariable Long id, @RequestBody Map<String, String> requestBody){
        CuadroDeObraEstado nuevoEstado = CuadroDeObraEstado.valueOf(requestBody.get("estado"));
        return ResponseEntity.ok(cuadroDeObraService.updateEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCuadro(@PathVariable Long id){
        cuadroDeObraService.deleteCuadro(id);
    }
}
