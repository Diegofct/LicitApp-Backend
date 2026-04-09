package com.elemental.licitapp.Empresa.infrastructure.in.controller;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaFinanciera;
import com.elemental.licitapp.Empresa.application.ports.in.EmpresaUseCase;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.IndicadoresCalculadosDTO;
import com.elemental.licitapp.Empresa.infrastructure.in.controller.dto.IndicadoresRawDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/empresas")
public class EmpresaController {

    private final EmpresaUseCase empresaUseCase;

    public EmpresaController(EmpresaUseCase empresaUseCase) {
        this.empresaUseCase = empresaUseCase;
    }

    @PostMapping("/calcular-indicadores")
    public ResponseEntity<IndicadoresCalculadosDTO> calcularIndicadores(@RequestBody IndicadoresRawDTO rawDTO) {
        var liquidez = ReglaFinanciera.calcularLiquidez(rawDTO.getActivoCorriente(), rawDTO.getPasivoCorriente());
        var endeudamiento = ReglaFinanciera.calcularEndeudamiento(rawDTO.getPasivoTotal(), rawDTO.getActivoTotal());
        var rci = ReglaFinanciera.calcularRCI(rawDTO.getUtilidadOperacional(), rawDTO.getGastosInteres());
        var roe = ReglaFinanciera.calcularROE(rawDTO.getUtilidadOperacional(), rawDTO.getPatrimonio());
        var roa = ReglaFinanciera.calcularROA(rawDTO.getUtilidadOperacional(), rawDTO.getActivoTotal());
        var capitalTrabajo = ReglaFinanciera.calcularCapitalTrabajo(rawDTO.getActivoCorriente(), rawDTO.getPasivoCorriente());

        var calculados = IndicadoresCalculadosDTO.builder()
                .liquidez(liquidez)
                .endeudamiento(endeudamiento)
                .razonCoberturaInteres(rci)
                .rentabilidadPatrimonio(roe)
                .rentabilidadActivo(roa)
                .capitalTrabajo(capitalTrabajo)
                .build();

        return ResponseEntity.ok(calculados);
    }

    @PostMapping
    public ResponseEntity<Empresa> crearEmpresa(@RequestBody Empresa empresa) {
        return ResponseEntity.ok(empresaUseCase.crearEmpresa(empresa));
    }

    @GetMapping
    public ResponseEntity<List<Empresa>> listarEmpresas() {
        return ResponseEntity.ok(empresaUseCase.obtenerTodas());
    }

    @GetMapping("/{nit}")
    public ResponseEntity<Empresa> obtenerPorNit(@PathVariable String nit) {
        return empresaUseCase.obtenerPorNit(nit)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizarEmpresa(@PathVariable Long id, @RequestBody Empresa empresa) {
        return ResponseEntity.ok(empresaUseCase.actualizarEmpresa(id, empresa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Empresa> eliminarEmpresa(@PathVariable Long id) {
        empresaUseCase.eliminarEmpresa(id);
        return ResponseEntity.notFound().build();
    }


}
