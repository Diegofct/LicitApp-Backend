package com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller;

import com.elemental.licitapp.CuadroDeObra.application.ports.in.CuadroDeObraUseCase;
import com.elemental.licitapp.CuadroDeObra.application.ports.in.ExtraerPliegoUseCase;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.ActualizarEstadoRequestDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.CuadroDeObraRequestDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.CuadroDeObraResponseDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.ExtraccionPliegoResponseDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.RequisitoLicitacionRequestDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.dto.RequisitoLicitacionResponseDTO;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.mapper.CuadroDeObraRequestMapper;
import com.elemental.licitapp.CuadroDeObra.infrastructure.in.controller.mapper.ExtraccionPliegoResponseMapper;
import com.elemental.licitapp.Exception.PliegoIlegibleException;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccionPliego;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("cuadro-de-obra")
public class CuadroDeObraController {

    private final CuadroDeObraUseCase cuadroDeObraUseCase;
    private final ExtraerPliegoUseCase extraerPliegoUseCase;

    public CuadroDeObraController(CuadroDeObraUseCase cuadroDeObraUseCase,
                                  ExtraerPliegoUseCase extraerPliegoUseCase) {
        this.cuadroDeObraUseCase = cuadroDeObraUseCase;
        this.extraerPliegoUseCase = extraerPliegoUseCase;
    }

    /**
     * Extrae con IA los requisitos habilitantes de un pliego en PDF y devuelve un borrador
     * editable (no persiste). El analista revisa/corrige y luego confirma con
     * POST /{id}/requisitos o PATCH /{id}/requisitos. Para controlar el costo, el analista
     * indica las paginas relevantes (los requisitos suelen estar dispersos en el pliego).
     */
    @PostMapping(value = "/{id}/extraer-pliego", consumes = "multipart/form-data")
    public ResponseEntity<ExtraccionPliegoResponseDTO> extraerPliego(
            @PathVariable Long id,
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("paginas") String paginas) {
        byte[] pdf;
        try {
            pdf = archivo.getBytes();
        } catch (IOException ex) {
            throw new PliegoIlegibleException("No se pudo leer el archivo subido.");
        }
        ResultadoExtraccionPliego resultado =
                extraerPliegoUseCase.extraerRequisitos(id, pdf, paginas, archivo.getOriginalFilename());
        return ResponseEntity.ok(ExtraccionPliegoResponseMapper.toResponse(resultado));
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

    @PatchMapping("/{id}/requisitos")
    public ResponseEntity<RequisitoLicitacionResponseDTO> updateRequisitos(@PathVariable Long id,
                                                                           @Valid @RequestBody RequisitoLicitacionRequestDTO requisitoDTO) {
        RequisitoLicitacion parche = CuadroDeObraRequestMapper.toEntity(requisitoDTO);
        RequisitoLicitacion actualizado = cuadroDeObraUseCase.actualizarRequisito(id, parche);
        return ResponseEntity.ok(CuadroDeObraRequestMapper.toResponseDTO(actualizado));
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
