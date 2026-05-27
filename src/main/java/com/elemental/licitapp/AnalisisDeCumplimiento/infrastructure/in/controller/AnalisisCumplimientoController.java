package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.CalcularCapacidadProcesoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.ConsultarConsorcioUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.EvaluarConformacionUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.EvaluarCumplimientoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.GuardarConformacionConsorcioUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.CalcularResidualRequestDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.ConsorcioResponseDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.EvaluarCumplimientoRequestDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.EvaluarCumplimientoResponseDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.dto.GuardarConsorcioRequestDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.mapper.AnalisisCumplimientoResponseMapper;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.in.controller.mapper.ConsorcioRequestMapper;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/analisis")
public class AnalisisCumplimientoController {

    private final EvaluarCumplimientoUseCase evaluarCumplimientoUseCase;
    private final EvaluarConformacionUseCase evaluarConformacionUseCase;
    private final CalcularCapacidadProcesoUseCase calcularCapacidadProcesoUseCase;
    private final GuardarConformacionConsorcioUseCase guardarConformacionUseCase;
    private final ConsultarConsorcioUseCase consultarConsorcioUseCase;

    public AnalisisCumplimientoController(EvaluarCumplimientoUseCase evaluarCumplimientoUseCase,
                                          EvaluarConformacionUseCase evaluarConformacionUseCase,
                                          CalcularCapacidadProcesoUseCase calcularCapacidadProcesoUseCase,
                                          GuardarConformacionConsorcioUseCase guardarConformacionUseCase,
                                          ConsultarConsorcioUseCase consultarConsorcioUseCase) {
        this.evaluarCumplimientoUseCase = evaluarCumplimientoUseCase;
        this.evaluarConformacionUseCase = evaluarConformacionUseCase;
        this.calcularCapacidadProcesoUseCase = calcularCapacidadProcesoUseCase;
        this.guardarConformacionUseCase = guardarConformacionUseCase;
        this.consultarConsorcioUseCase = consultarConsorcioUseCase;
    }

    /**
     * Evalúa si una empresa cumple los requisitos del pliego (modo INDIVIDUAL).
     * Si no cumple sola, sugiere consorcios viables simulando con cada otra
     * empresa registrada usando porcentajeSimulacion (default 0.5) para la
     * empresa solicitante y el complemento para la candidata.
     */
    @PostMapping("/evaluar")
    public ResponseEntity<EvaluarCumplimientoResponseDTO> evaluar(
            @Valid @RequestBody EvaluarCumplimientoRequestDTO request) {

        ResultadoEvaluacion resultado = evaluarCumplimientoUseCase.evaluar(
            request.getEmpresaId(),
            request.getCuadroDeObraId(),
            TipoParticipacion.INDIVIDUAL,
            request.getPorcentajeSimulacion()
        );

        return ResponseEntity.ok(AnalisisCumplimientoResponseMapper.toResponse(resultado));
    }

    /**
     * Evalúa la ConformacionConsorcio ya registrada para el cuadro, aplicando
     * los porcentajes reales pactados entre los integrantes (no la heurística
     * 50/50 de la sugerencia).
     */
    @PostMapping("/conformaciones/{cuadroDeObraId}/evaluar")
    public ResponseEntity<EvaluarCumplimientoResponseDTO> evaluarConformacion(
            @PathVariable Long cuadroDeObraId) {
        ResultadoEvaluacion resultado = evaluarConformacionUseCase.evaluarConformacion(cuadroDeObraId);
        return ResponseEntity.ok(AnalisisCumplimientoResponseMapper.toResponse(resultado));
    }

    /**
     * CRPC = Presupuesto - Anticipo (plazos ≤ 12 meses).
     */
    @PostMapping("/calcular-residual-proceso-contratacion")
    public ResponseEntity<BigDecimal> calcularResidualProceso(
            @Valid @RequestBody CalcularResidualRequestDTO request) {
        return ResponseEntity.ok(calcularCapacidadProcesoUseCase.calcularCRPC(
                request.getPresupuestoOficial(),
                request.getAnticipo()));
    }

    /**
     * CTd = (Presupuesto - Anticipo) × 33%.
     */
    @PostMapping("/calcular-capital-trabajo-demandado")
    public ResponseEntity<BigDecimal> calcularCTd(
            @Valid @RequestBody CalcularResidualRequestDTO request) {
        return ResponseEntity.ok(calcularCapacidadProcesoUseCase.calcularCTd(
                request.getPresupuestoOficial(),
                request.getAnticipo()));
    }

    /**
     * Persiste la conformación del proponente (individual / consorcio / UT) que se
     * presentará a un cuadro de obra. Si ya existía conformación para ese cuadro,
     * se reemplaza.
     */
    @PostMapping("/consorcios")
    public ResponseEntity<ConsorcioResponseDTO> guardarConsorcio(
            @Valid @RequestBody GuardarConsorcioRequestDTO request) {
        ConformacionConsorcio entity = ConsorcioRequestMapper.toEntity(request);
        ConformacionConsorcio guardada = guardarConformacionUseCase.guardar(entity);
        return ResponseEntity.ok(ConsorcioRequestMapper.toResponse(guardada));
    }

    @GetMapping("/consorcios/cuadro/{cuadroDeObraId}")
    public ResponseEntity<ConsorcioResponseDTO> obtenerConsorcioPorCuadro(
            @PathVariable Long cuadroDeObraId) {
        return consultarConsorcioUseCase.buscarPorCuadroDeObra(cuadroDeObraId)
                .map(ConsorcioRequestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe conformación de consorcio para el cuadro " + cuadroDeObraId));
    }
}
