package com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller;

import com.elemental.licitapp.Exception.ResourceNotFoundException;
import com.elemental.licitapp.SeguimientoProceso.application.ports.in.ConsultarSeguimientoUseCase;
import com.elemental.licitapp.SeguimientoProceso.application.ports.in.RegistrarEventoUseCase;
import com.elemental.licitapp.SeguimientoProceso.domain.entity.EventoSeguimiento;
import com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto.EventoSeguimientoResponseDTO;
import com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto.RegistrarEventoRequestDTO;
import com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.dto.SeguimientoResponseDTO;
import com.elemental.licitapp.SeguimientoProceso.infrastructure.in.controller.mapper.SeguimientoRequestMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seguimientos")
public class SeguimientoController {

    private final RegistrarEventoUseCase registrarEventoUseCase;
    private final ConsultarSeguimientoUseCase consultarSeguimientoUseCase;

    public SeguimientoController(RegistrarEventoUseCase registrarEventoUseCase,
                                 ConsultarSeguimientoUseCase consultarSeguimientoUseCase) {
        this.registrarEventoUseCase = registrarEventoUseCase;
        this.consultarSeguimientoUseCase = consultarSeguimientoUseCase;
    }

    @PostMapping("/cuadro/{cuadroDeObraId}/eventos")
    public ResponseEntity<EventoSeguimientoResponseDTO> registrarEvento(
            @PathVariable Long cuadroDeObraId,
            @Valid @RequestBody RegistrarEventoRequestDTO request) {
        EventoSeguimiento evento = SeguimientoRequestMapper.toEntity(request);
        EventoSeguimiento guardado = registrarEventoUseCase.registrar(cuadroDeObraId, evento);
        return ResponseEntity.ok(SeguimientoRequestMapper.toResponse(guardado));
    }

    @GetMapping("/cuadro/{cuadroDeObraId}")
    public ResponseEntity<SeguimientoResponseDTO> obtenerPorCuadro(
            @PathVariable Long cuadroDeObraId) {
        return consultarSeguimientoUseCase.obtenerPorCuadroDeObra(cuadroDeObraId)
                .map(SeguimientoRequestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe seguimiento para el cuadro " + cuadroDeObraId));
    }
}
