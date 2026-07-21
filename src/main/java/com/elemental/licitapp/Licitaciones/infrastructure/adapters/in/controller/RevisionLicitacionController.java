package com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller;

import com.elemental.licitapp.Licitaciones.application.ports.in.RevisionLicitacionUseCase;
import com.elemental.licitapp.Licitaciones.infrastructure.adapters.in.controller.dto.MarcarRevisionRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Marca "Revisado" de la Busqueda SECOP, compartida por el equipo. El conjunto de
 * idDelProceso revisados se persiste en el servidor; el frontend lo cruza contra las
 * licitaciones para resaltar las revisadas.
 *
 * <p>Cuelga de /licitaciones, ya autorizado a ANALISTA/ADMIN en SecurityConfig.
 */
@RestController
@RequestMapping("/licitaciones/revisiones")
public class RevisionLicitacionController {

    private final RevisionLicitacionUseCase revisionLicitacionUseCase;

    public RevisionLicitacionController(RevisionLicitacionUseCase revisionLicitacionUseCase) {
        this.revisionLicitacionUseCase = revisionLicitacionUseCase;
    }

    @GetMapping
    public ResponseEntity<List<String>> obtenerRevisiones() {
        return ResponseEntity.ok(revisionLicitacionUseCase.obtenerRevisiones());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void marcar(@Valid @RequestBody MarcarRevisionRequestDTO request) {
        revisionLicitacionUseCase.marcarRevisada(request.getIdDelProceso());
    }

    // idDelProceso de SECOP II (ej. "CO1.NO1.7654321") lleva puntos; Spring Boot 3 usa
    // PathPattern y no trunca en el punto, asi que es seguro como PathVariable.
    @DeleteMapping("/{idDelProceso}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void desmarcar(@PathVariable String idDelProceso) {
        revisionLicitacionUseCase.desmarcarRevisada(idDelProceso);
    }
}
