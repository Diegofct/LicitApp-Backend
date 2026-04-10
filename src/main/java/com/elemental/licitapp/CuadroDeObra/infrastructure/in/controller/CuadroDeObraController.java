package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller;

import com.elemental.licitapp.CuadroDeObra.application.service.CuadroDeObraService;
import com.elemental.licitapp.CuadroDeObra.application.service.RequisitoAIService;
import com.elemental.licitapp.CuadroDeObra.domain.dto.RequisitoExtraidoDTO;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("cuadro-de-obra")
public class CuadroDeObraController {

    private final CuadroDeObraService cuadroDeObraService;
    private final RequisitoAIService requisitoAIService;

    public CuadroDeObraController(CuadroDeObraService cuadroDeObraService,
                                 RequisitoAIService requisitoAIService) {
        this.cuadroDeObraService = cuadroDeObraService;
        this.requisitoAIService = requisitoAIService;
    }
    @PostMapping("/{id}/extraer-requisitos")
    public ResponseEntity<RequisitoExtraidoDTO> extraerRequisitos(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(requisitoAIService.extraerRequisitosDePliego(id, file));
    }

    // Endpoints para Requisitos
    @GetMapping("/{id}/requisitos")
    public ResponseEntity<RequisitoLicitacion> getRequisitos(@PathVariable Long id) {
        return ResponseEntity.ok(cuadroDeObraService.getRequisitoByCuadroId(id));
    }

    @PostMapping("/{id}/requisitos")
    public ResponseEntity<RequisitoLicitacion> saveRequisitos(@PathVariable Long id, @RequestBody RequisitoLicitacion requisito) {
        return new ResponseEntity<>(cuadroDeObraService.saveRequisito(id, requisito), HttpStatus.CREATED);
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
        String estadoString = requestBody.get("cuadroDeObraEstado");
        CuadroDeObraEstado nuevoEstado = CuadroDeObraEstado.valueOf(estadoString);
        return ResponseEntity.ok(cuadroDeObraService.updateEstado(id, nuevoEstado));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCuadro(@PathVariable Long id){
        cuadroDeObraService.deleteCuadro(id);
    }
}
