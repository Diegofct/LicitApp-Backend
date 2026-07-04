package com.elemental.licitapp.Empresa.infrastructure.in.controller;

import com.elemental.licitapp.Empresa.application.ports.in.EmpresaUseCase;
import com.elemental.licitapp.Empresa.application.ports.in.ExtraerRupUseCase;
import com.elemental.licitapp.Empresa.domain.entity.CapacidadResidualProponente;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Empresa.domain.entity.IndicadoresFinancieros;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.CapacidadResidualCalculadaDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.CapacidadResidualProponenteRequestDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.EmpresaRequestDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.EmpresaResponseDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.ExtraccionRupResponseDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.IndicadoresCalculadosDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.IndicadoresFinancierosRequestDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.mapper.EmpresaRequestMapper;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.mapper.ExtraccionRupResponseMapper;
import com.elemental.licitapp.Exception.PliegoIlegibleException;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccion;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaUseCase empresaUseCase;
    private final ExtraerRupUseCase extraerRupUseCase;

    public EmpresaController(EmpresaUseCase empresaUseCase, ExtraerRupUseCase extraerRupUseCase) {
        this.empresaUseCase = empresaUseCase;
        this.extraerRupUseCase = extraerRupUseCase;
    }

    /**
     * Extrae con IA los datos de un RUP en PDF y devuelve un borrador editable (no persiste).
     * El analista revisa/corrige y luego confirma con POST /empresas o PUT /empresas/{id}.
     */
    @PostMapping(value = "/extraer-rup", consumes = "multipart/form-data")
    public ResponseEntity<ExtraccionRupResponseDTO> extraerRup(@RequestParam("archivo") MultipartFile archivo) {
        byte[] pdf;
        try {
            pdf = archivo.getBytes();
        } catch (IOException ex) {
            throw new PliegoIlegibleException("No se pudo leer el archivo subido.");
        }
        ResultadoExtraccion resultado = extraerRupUseCase.extraerDatosRup(pdf, archivo.getOriginalFilename());
        return ResponseEntity.ok(ExtraccionRupResponseMapper.toResponse(resultado));
    }

    /**
     * Calcula indicadores derivados sin persistir (usado por el formulario de Angular
     * para mostrar liquidez/endeudamiento/etc. en vivo).
     */
    @PostMapping("/calcular-indicadores")
    public ResponseEntity<IndicadoresCalculadosDTO> calcularIndicadores(
            @Valid @RequestBody IndicadoresFinancierosRequestDTO request) {
        IndicadoresFinancieros ind = EmpresaRequestMapper.toEntity(request);
        ind.recalcular();

        return ResponseEntity.ok(IndicadoresCalculadosDTO.builder()
                .liquidez(ind.getLiquidez())
                .estadoLiquidez(ind.getEstadoLiquidez())
                .endeudamiento(ind.getEndeudamiento())
                .estadoEndeudamiento(ind.getEstadoEndeudamiento())
                .razonCoberturaInteres(ind.getRazonCoberturaInteres())
                .estadoRazonCoberturaInteres(ind.getEstadoRazonCoberturaInteres())
                .rentabilidadPatrimonio(ind.getRentabilidadPatrimonio())
                .estadoRentabilidadPatrimonio(ind.getEstadoRentabilidadPatrimonio())
                .rentabilidadActivo(ind.getRentabilidadActivo())
                .estadoRentabilidadActivo(ind.getEstadoRentabilidadActivo())
                .capitalTrabajo(ind.getCapitalTrabajo())
                .build());
    }

    /**
     * Calcula el resultadoK (CRP del proponente) sin persistir. Permite al
     * formulario de Angular mostrar el valor en vivo, incluso en modo creación
     * cuando aún no existe empresaId.
     */
    @PostMapping("/calcular-capacidad-residual")
    public ResponseEntity<CapacidadResidualCalculadaDTO> calcularCapacidadResidual(
            @Valid @RequestBody CapacidadResidualProponenteRequestDTO dto) {
        CapacidadResidualProponente capacidad = EmpresaRequestMapper.toEntity(dto);
        capacidad.recalcular();
        return ResponseEntity.ok(CapacidadResidualCalculadaDTO.builder()
                .resultadoCapacidadResidualProponente(capacidad.getResultadoK())
                .build());
    }

    @PostMapping("/{id}/capacidad-residual")
    public ResponseEntity<EmpresaResponseDTO> guardarCapacidadResidual(
            @PathVariable Long id,
            @Valid @RequestBody CapacidadResidualProponenteRequestDTO dto) {
        CapacidadResidualProponente capacidad = EmpresaRequestMapper.toEntity(dto);
        Empresa empresa = empresaUseCase.guardarCapacidadResidual(id, capacidad);
        return ResponseEntity.ok(EmpresaRequestMapper.toResponse(empresa));
    }

    @PostMapping
    public ResponseEntity<EmpresaResponseDTO> crearEmpresa(@Valid @RequestBody EmpresaRequestDTO empresaDTO) {
        Empresa empresa = EmpresaRequestMapper.toEntity(empresaDTO);
        return ResponseEntity.ok(EmpresaRequestMapper.toResponse(empresaUseCase.crearEmpresa(empresa)));
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponseDTO>> listarEmpresas() {
        List<EmpresaResponseDTO> respuesta = empresaUseCase.obtenerTodas().stream()
                .map(EmpresaRequestMapper::toResponse)
                .toList();
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/{nit}")
    public ResponseEntity<EmpresaResponseDTO> obtenerPorNit(@PathVariable String nit) {
        return empresaUseCase.obtenerPorNit(nit)
                .map(EmpresaRequestMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmpresaResponseDTO> actualizarEmpresa(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaRequestDTO empresaDTO) {
        Empresa empresa = EmpresaRequestMapper.toEntity(empresaDTO);
        return ResponseEntity.ok(EmpresaRequestMapper.toResponse(empresaUseCase.actualizarEmpresa(id, empresa)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) {
        empresaUseCase.eliminarEmpresa(id);
        return ResponseEntity.noContent().build();
    }
}
