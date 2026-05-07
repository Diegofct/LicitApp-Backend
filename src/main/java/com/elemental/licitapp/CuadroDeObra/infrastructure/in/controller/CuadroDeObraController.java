package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller;

import com.elemental.licitapp.CuadroDeObra.application.ports.in.CuadroDeObraUseCase;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.ActualizarEstadoRequestDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.CuadroDeObraRequestDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.CuadroDeObraResponseDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.RequisitoLicitacionRequestDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.RequisitoLicitacionResponseDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.mapper.CuadroDeObraRequestMapper;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("cuadro-de-obra")
public class CuadroDeObraController {

    private final CuadroDeObraUseCase cuadroDeObraUseCase;

    public CuadroDeObraController(CuadroDeObraUseCase cuadroDeObraUseCase) {
        this.cuadroDeObraUseCase = cuadroDeObraUseCase;
    }

    // Endpoints para Requisitos
    @GetMapping("/{id}/requisitos")
    public ResponseEntity<RequisitoLicitacionResponseDTO> getRequisitos(@PathVariable Long id) {
        return ResponseEntity.ok(
                CuadroDeObraRequestMapper.toResponseDTO(cuadroDeObraUseCase.getRequisitoByCuadroId(id)));
    }

    @PostMapping("/{id}/requisitos")
    public ResponseEntity<RequisitoLicitacionResponseDTO> saveRequisitos(@PathVariable Long id,
                                                                         @Valid @RequestBody RequisitoLicitacionRequestDTO requisitoDTO) {
        RequisitoLicitacion requisito = CuadroDeObraRequestMapper.toEntity(requisitoDTO);
        RequisitoLicitacion guardado = cuadroDeObraUseCase.saveRequisito(id, requisito);
        return new ResponseEntity<>(CuadroDeObraRequestMapper.toResponseDTO(guardado), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<CuadroDeObraResponseDTO>> getCuadrosPorVista(@RequestParam(defaultValue = "por-presentar") String vista, Pageable pageable) {
        Page<CuadroDeObraResponseDTO> page = cuadroDeObraUseCase.findCuadrosPorVistas(vista, pageable)
                .map(CuadroDeObraRequestMapper::toResponseDTO);
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CuadroDeObraResponseDTO> getCuadroById(@PathVariable Long id) {
        return ResponseEntity.ok(
                CuadroDeObraRequestMapper.toResponseDTO(cuadroDeObraUseCase.findCuadroById(id)));
    }

    @PostMapping
    public ResponseEntity<CuadroDeObraResponseDTO> createCuadro(@Valid @RequestBody CuadroDeObraRequestDTO nuevoCuadroDTO) {
        var cuadroCreado = cuadroDeObraUseCase.createCuadro(CuadroDeObraRequestMapper.toEntity(nuevoCuadroDTO));
        return new ResponseEntity<>(CuadroDeObraRequestMapper.toResponseDTO(cuadroCreado), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuadroDeObraResponseDTO> updateCuadro(@PathVariable Long id,
                                                                @Valid @RequestBody CuadroDeObraRequestDTO cuadroDTO) {
        var actualizado = cuadroDeObraUseCase.updateCuadro(id, CuadroDeObraRequestMapper.toEntity(cuadroDTO));
        return ResponseEntity.ok(CuadroDeObraRequestMapper.toResponseDTO(actualizado));
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<CuadroDeObraResponseDTO> updateEstado(@PathVariable Long id,
                                                                @Valid @RequestBody ActualizarEstadoRequestDTO request) {
        var actualizado = cuadroDeObraUseCase.updateEstado(id, request.getCuadroDeObraEstado());
        return ResponseEntity.ok(CuadroDeObraRequestMapper.toResponseDTO(actualizado));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCuadro(@PathVariable Long id){
        cuadroDeObraUseCase.deleteCuadro(id);
    }
}
