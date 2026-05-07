package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.TipoParticipacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.in.EvaluarCumplimientoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaCapacidadResidual;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/analisis")
@RequiredArgsConstructor
public class AnalisisCumplimientoController {

    private final EvaluarCumplimientoUseCase evaluarCumplimientoUseCase;

    /**
     * Evalúa si una empresa cumple con los requisitos financieros y de experiencia de una licitación.
     */
    @PostMapping("/evaluar")
    public ResponseEntity<EvaluarCumplimientoResponseDTO> evaluar(
            @Valid @RequestBody EvaluarCumplimientoRequestDTO requestDTO) {

        ResultadoEvaluacion resultado = evaluarCumplimientoUseCase.evaluar(
            requestDTO.getEmpresaId(),
            requestDTO.getCuadroDeObraId(),
            TipoParticipacion.INDIVIDUAL
        );

        List<DetalleRequisitoDTO> detallesDTO = resultado.detalles().stream()
            .map(d -> new DetalleRequisitoDTO(d.nombre(), d.valorRequerido(), d.valorActual(), d.cumple(), d.mensaje()))
            .toList();

        List<SugerenciaConsorcioDTO> sugerenciasDTO = new ArrayList<>();
        if (resultado.sugerencias() != null) {
            sugerenciasDTO = resultado.sugerencias().stream()
                .map(s -> new SugerenciaConsorcioDTO(s.empresaId(), s.nit(), s.razonSocial()))
                .toList();
        }

        return ResponseEntity.ok(new EvaluarCumplimientoResponseDTO(
            resultado.empresaId(),
            resultado.cuadroId(),
            resultado.tipo().name(),
            resultado.cumpleGlobal(),
            detallesDTO,
            sugerenciasDTO
        ));
    }

    /**
     * CRPC = Presupuesto - Anticipo (plazos <= 12 meses).
     */
    @PostMapping("/calcular-residual-proceso-contratacion")
    public ResponseEntity<BigDecimal> calcularResidualProceso(
            @Valid @RequestBody CalcularCapacidadProcesoRequestDTO request) {
        return ResponseEntity.ok(ReglaCapacidadResidual.calcularCRPC(
                request.getPresupuestoOficial(),
                request.getAnticipo()));
    }

    /**
     * CTd = (Presupuesto - Anticipo) * 33%.
     */
    @PostMapping("/calcular-capital-trabajo-demandado")
    public ResponseEntity<BigDecimal> calcularCTd(
            @Valid @RequestBody CalcularCTdRequestDTO request) {
        return ResponseEntity.ok(ReglaCapacidadResidual.calcularCTd(
                request.getPresupuestoOficial(),
                request.getAnticipo()));
    }
}
