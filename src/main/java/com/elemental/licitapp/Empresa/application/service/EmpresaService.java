package com.elemental.licitapp.Empresa.application.service;

import com.elemental.licitapp.Empresa.application.ports.in.ConsultarEmpresasUseCase;
import com.elemental.licitapp.Empresa.application.ports.in.EmpresaUseCase;
import com.elemental.licitapp.Empresa.application.ports.out.EmpresaRepositoryPort;
import com.elemental.licitapp.Empresa.domain.entity.CapacidadResidualProponente;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService implements EmpresaUseCase, ConsultarEmpresasUseCase {

    private final EmpresaRepositoryPort empresaRepositoryPort;

    public EmpresaService(EmpresaRepositoryPort empresaRepositoryPort) {
        this.empresaRepositoryPort = empresaRepositoryPort;
    }

    @Override
    @Transactional
    public Empresa crearEmpresa(Empresa empresa) {
        if (empresa.getIndicadores() != null) {
            empresa.getIndicadores().setEmpresa(empresa);
            empresa.getIndicadores().recalcular();
        }
        if (empresa.getExperiencias() != null) {
            empresa.getExperiencias().forEach(exp -> exp.setEmpresa(empresa));
        }
        if (empresa.getCapacidadesResiduales() != null) {
            empresa.getCapacidadesResiduales().removeIf(c -> !tieneAlgunComponente(c));
            empresa.getCapacidadesResiduales().forEach(cap -> {
                cap.setEmpresa(empresa);
                cap.recalcular();
            });
        }
        return empresaRepositoryPort.guardar(empresa);
    }

    @Override
    public List<Empresa> obtenerTodas() {
        return empresaRepositoryPort.listarTodas();
    }

    @Override
    public Optional<Empresa> obtenerPorId(Long id) {
        return empresaRepositoryPort.buscarPorId(id);
    }

    @Override
    public Optional<Empresa> obtenerPorNit(String nit) {
        return empresaRepositoryPort.buscarPorNit(nit);
    }

    @Override
    public void eliminarEmpresa(Long id) {
        empresaRepositoryPort.eliminar(id);
    }

    @Override
    @Transactional
    public Empresa actualizarEmpresa(Long id, Empresa empresaActualizada) {
        Empresa empresaExistente = empresaRepositoryPort.buscarPorId(id)
                .or(() -> empresaRepositoryPort.buscarPorNit(String.valueOf(id)))
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID/NIT: " + id));

        if (empresaActualizada.getNit() != null) {
            empresaExistente.setNit(empresaActualizada.getNit());
        }
        if (empresaActualizada.getNumeroProponenteCcb() != null) {
            empresaExistente.setNumeroProponenteCcb(empresaActualizada.getNumeroProponenteCcb());
        }
        if (empresaActualizada.getIdentificacionRepresentanteLegal() != null) {
            empresaExistente.setIdentificacionRepresentanteLegal(empresaActualizada.getIdentificacionRepresentanteLegal());
        }
        if (empresaActualizada.getFechaInscripcion() != null) {
            empresaExistente.setFechaInscripcion(empresaActualizada.getFechaInscripcion());
        }
        if (empresaActualizada.getFechaUltimaRenovacion() != null) {
            empresaExistente.setFechaUltimaRenovacion(empresaActualizada.getFechaUltimaRenovacion());
        }

        empresaExistente.setRazonSocial(empresaActualizada.getRazonSocial());
        empresaExistente.setDireccion(empresaActualizada.getDireccion());
        empresaExistente.setTelefono(empresaActualizada.getTelefono());
        empresaExistente.setCorreo(empresaActualizada.getCorreo());
        empresaExistente.setTamanoEmpresa(empresaActualizada.getTamanoEmpresa());
        empresaExistente.setRepresentanteLegal(empresaActualizada.getRepresentanteLegal());

        if (empresaActualizada.getIndicadores() != null) {
            var indNuevos = empresaActualizada.getIndicadores();
            var indExistentes = empresaExistente.getIndicadores();

            if (indExistentes == null) {
                indNuevos.setEmpresa(empresaExistente);
                empresaExistente.setIndicadores(indNuevos);
            } else {
                indExistentes.setAnioCierre(indNuevos.getAnioCierre());
                indExistentes.setActivoCorriente(indNuevos.getActivoCorriente());
                indExistentes.setPasivoCorriente(indNuevos.getPasivoCorriente());
                indExistentes.setActivoTotal(indNuevos.getActivoTotal());
                indExistentes.setPasivoTotal(indNuevos.getPasivoTotal());
                indExistentes.setUtilidadOperacional(indNuevos.getUtilidadOperacional());
                indExistentes.setGastosInteres(indNuevos.getGastosInteres());
            }
            empresaExistente.getIndicadores().recalcular();
        }

        if (empresaActualizada.getExperiencias() != null) {
            empresaExistente.getExperiencias().clear();
            empresaActualizada.getExperiencias().forEach(exp -> {
                exp.setEmpresa(empresaExistente);
                empresaExistente.getExperiencias().add(exp);
            });
        }

        if (empresaActualizada.getCapacidadesResiduales() != null) {
            empresaExistente.getCapacidadesResiduales().clear();
            empresaActualizada.getCapacidadesResiduales().stream()
                    .filter(EmpresaService::tieneAlgunComponente)
                    .forEach(cap -> {
                        cap.setEmpresa(empresaExistente);
                        cap.recalcular();
                        empresaExistente.getCapacidadesResiduales().add(cap);
                    });
        }

        return empresaRepositoryPort.guardar(empresaExistente);
    }

    @Override
    @Transactional
    public Empresa guardarCapacidadResidual(Long empresaId, CapacidadResidualProponente capacidad) {
        if (!tieneAlgunComponente(capacidad)) {
            throw new IllegalArgumentException(
                    "No se puede guardar una capacidad residual con todos los componentes en cero");
        }

        Empresa empresa = empresaRepositoryPort.buscarPorId(empresaId)
                .or(() -> empresaRepositoryPort.buscarPorNit(String.valueOf(empresaId)))
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID/NIT: " + empresaId));

        capacidad.setEmpresa(empresa);
        capacidad.recalcular();

        if (capacidad.getId() != null) {
            empresa.getCapacidadesResiduales().removeIf(c -> c.getId().equals(capacidad.getId()));
        }
        empresa.getCapacidadesResiduales().add(capacidad);

        return empresaRepositoryPort.guardar(empresa);
    }

    private static boolean tieneAlgunComponente(CapacidadResidualProponente c) {
        return esPositivo(c.getCapacidadOrganizacion())
                || esPositivo(c.getExperiencia())
                || esPositivo(c.getCapacidadTecnica())
                || esPositivo(c.getCapacidadFinanciera())
                || esPositivo(c.getSaldosContratosEjecucion());
    }

    private static boolean esPositivo(BigDecimal v) {
        return v != null && v.compareTo(BigDecimal.ZERO) > 0;
    }
}
