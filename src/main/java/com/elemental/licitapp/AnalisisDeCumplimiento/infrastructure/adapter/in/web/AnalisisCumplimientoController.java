package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.TipoParticipacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.in.EvaluarCumplimientoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto.DetalleRequisitoDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto.EvaluarCumplimientoRequestDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto.EvaluarCumplimientoResponseDTO;
import com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.in.web.dto.SugerenciaConsorcioDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * 
     * @param requestDTO Contiene el ID de la empresa base y el ID del cuadro de obra (licitación)
     * @return Resultado detallado del análisis con semáforo de cumplimiento.
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
}
