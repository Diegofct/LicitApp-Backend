package com.elemental.licitapp.Resultados.infrastructure.in.controller;

import com.elemental.licitapp.Resultados.application.ports.in.ConsultarHistorialResultadosUseCase;
import com.elemental.licitapp.Resultados.application.ports.in.ConsultarResumenResultadosUseCase;
import com.elemental.licitapp.Resultados.infrastructure.in.controller.dto.ItemHistorialResultadoResponseDTO;
import com.elemental.licitapp.Resultados.infrastructure.in.controller.dto.ResumenResultadosResponseDTO;
import com.elemental.licitapp.Resultados.infrastructure.in.controller.mapper.ResultadosResponseMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resultados")
public class ResultadosController {

    private final ConsultarResumenResultadosUseCase consultarResumenUseCase;
    private final ConsultarHistorialResultadosUseCase consultarHistorialUseCase;

    public ResultadosController(ConsultarResumenResultadosUseCase consultarResumenUseCase,
                                ConsultarHistorialResultadosUseCase consultarHistorialUseCase) {
        this.consultarResumenUseCase = consultarResumenUseCase;
        this.consultarHistorialUseCase = consultarHistorialUseCase;
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenResultadosResponseDTO> obtenerResumen() {
        return ResponseEntity.ok(
                ResultadosResponseMapper.toResponse(consultarResumenUseCase.obtenerResumen()));
    }

    @GetMapping("/historial")
    public ResponseEntity<Page<ItemHistorialResultadoResponseDTO>> obtenerHistorial(Pageable pageable) {
        Page<ItemHistorialResultadoResponseDTO> page = consultarHistorialUseCase.obtenerHistorial(pageable)
                .map(ResultadosResponseMapper::toResponse);
        return ResponseEntity.ok(page);
    }
}
