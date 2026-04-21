package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.TipoParticipacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.in.EvaluarCumplimientoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaCapacidadResidual;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analisis")
@RequiredArgsConstructor
public class AnalisisCumplimientoController {

    private final EvaluarCumplimientoUseCase evaluarCumplimientoUseCase;

    /**
     * Endpoint para evaluar si una empresa cumple con los requisitos financieros y de experiencia de una licitación.
     */
    @PostMapping("/evaluar")
    public ResponseEntity<EvaluarCumplimientoResponseDTO> evaluar(
            @RequestBody EvaluarCumplimientoRequestDTO requestDTO) {
        
        ResultadoEvaluacion resultado = evaluarCumplimientoUseCase.evaluar(
            requestDTO.getEmpresaId(), 
            requestDTO.getCuadroDeObraId(), 
            TipoParticipacion.INDIVIDUAL
        );
        
        List<DetalleRequisitoDTO> detallesDTO = resultado.detalles().stream()
            .map(d -> new DetalleRequisitoDTO(
                d.nombre(), 
                d.valorRequerido(), 
                d.valorActual(), 
                d.cumple(), 
                d.mensaje()
            )).collect(Collectors.toList());

        List<SugerenciaConsorcioDTO> sugerenciasDTO = new ArrayList<>();
        if (resultado.sugerencias() != null) {
            sugerenciasDTO = resultado.sugerencias().stream()
                .map(s -> new SugerenciaConsorcioDTO(s.empresaId(), s.nit(), s.razonSocial()))
                .collect(Collectors.toList());
        }

        EvaluarCumplimientoResponseDTO responseDTO = new EvaluarCumplimientoResponseDTO(
            resultado.empresaId(),
            resultado.cuadroId(),
            resultado.tipo().name(),
            resultado.cumpleGlobal(),
            detallesDTO,
            sugerenciasDTO
        );

        return ResponseEntity.ok(responseDTO);
    }

    /**
     * Endpoint para simular el cálculo de la Capacidad Residual del Proponente (CRP).
     * Ruta: /analisis/calcular-residual-proponente
     */
    @PostMapping("/calcular-residual-proponente")
    public ResponseEntity<BigDecimal> calcularResidualProponente(
            @RequestBody CalcularCapacidadResidualRequestDTO request) {

        BigDecimal crp = ReglaCapacidadResidual.calcularCRP(
            request.getCapacidadOrganizacion(),
            request.getPuntajeExperiencia(),
            request.getPuntajeTecnico(),
            request.getPuntajeFinanciero(),
            request.getSaldosContratosEjecucion()
        );

        return ResponseEntity.ok(crp);
    }

    /**
     * Endpoint para calcular la Capacidad Residual del Proceso (CRPC).
     * Ruta: /analisis/calcular-residual-proceso-contratacion
     */
    @PostMapping("/calcular-residual-proceso-contratacion")
    public ResponseEntity<BigDecimal> calcularResidualProceso(
            @RequestBody CalcularCapacidadProcesoRequestDTO request) {

        BigDecimal crpc = ReglaCapacidadResidual.calcularCRPC(
            request.getPresupuestoOficial(), 
            request.getAnticipo()
        );

        return ResponseEntity.ok(crpc);
    }

    /**
     * Endpoint para calcular el Capital de Trabajo Demandado (CTd).
     * Ruta: /analisis/calcular-capital-trabajo-demandado
     */
    @PostMapping("/calcular-capital-trabajo-demandado")
    public ResponseEntity<BigDecimal> calcularCTd(
            @RequestBody CalcularCTdRequestDTO request) {

        BigDecimal ctd = ReglaCapacidadResidual.calcularCTd(
            request.getPresupuestoOficial(), 
            request.getAnticipo()
        );

        return ResponseEntity.ok(ctd);
    }
}
