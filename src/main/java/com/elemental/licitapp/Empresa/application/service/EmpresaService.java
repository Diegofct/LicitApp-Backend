package com.elemental.licitapp.Empresa.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaFinanciera;
import com.elemental.licitapp.Empresa.application.ports.in.EmpresaUseCase;
import com.elemental.licitapp.Empresa.application.ports.out.EmpresaRepositoryPort;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Empresa.domain.entity.IndicadoresFinancieros;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService implements EmpresaUseCase {

    private final EmpresaRepositoryPort empresaRepositoryPort;

    public EmpresaService(EmpresaRepositoryPort empresaRepositoryPort) {
        this.empresaRepositoryPort = empresaRepositoryPort;
    }

    @Override
    @Transactional
    public Empresa crearEmpresa(Empresa empresa) {
        // Aseguramos la relación bidireccional si vienen datos de indicadores o experiencia
        if (empresa.getIndicadores() != null) {
            empresa.getIndicadores().setEmpresa(empresa);
            // Calculamos automáticamente los indicadores financieros base
            this.recalcularIndicadores(empresa.getIndicadores());
        }
        if (empresa.getExperiencias() != null) {
            empresa.getExperiencias().forEach(exp -> exp.setEmpresa(empresa));
        }
        return empresaRepositoryPort.guardar(empresa);
    }

    private void recalcularIndicadores(IndicadoresFinancieros ind) {
        if (ind.getActivoCorriente() != null && ind.getPasivoCorriente() != null) {
            ind.setLiquidez(ReglaFinanciera.calcularLiquidez(ind.getActivoCorriente(), ind.getPasivoCorriente()).doubleValue());
            ind.setCapitalTrabajo(ReglaFinanciera.calcularCapitalTrabajo(ind.getActivoCorriente(), ind.getPasivoCorriente()));
        }
        
        if (ind.getPasivoTotal() != null && ind.getActivoTotal() != null) {
            ind.setEndeudamiento(ReglaFinanciera.calcularEndeudamiento(ind.getPasivoTotal(), ind.getActivoTotal()).doubleValue());
            ind.setPatrimonio(ind.getActivoTotal().subtract(ind.getPasivoTotal()));
        }

        if (ind.getUtilidadOperacional() != null && ind.getGastosInteres() != null) {
            ind.setRazonCoberturaInteres(ReglaFinanciera.calcularRCI(ind.getUtilidadOperacional(), ind.getGastosInteres()).doubleValue());
        }

        if (ind.getUtilidadOperacional() != null && ind.getPatrimonio() != null) {
            ind.setRentabilidadPatrimonio(ReglaFinanciera.calcularROE(ind.getUtilidadOperacional(), ind.getPatrimonio()).doubleValue());
        }

        if (ind.getUtilidadOperacional() != null && ind.getActivoTotal() != null) {
            ind.setRentabilidadActivo(ReglaFinanciera.calcularROA(ind.getUtilidadOperacional(), ind.getActivoTotal()).doubleValue());
        }
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
        // Primero intentamos buscar por ID de base de datos
        Optional<Empresa> empresaOpt = empresaRepositoryPort.buscarPorId(id);
        
        // Si no se encuentra por ID, intentamos buscar por NIT (en caso de que el frontend envíe el NIT como ID)
        if (empresaOpt.isEmpty()) {
            empresaOpt = empresaRepositoryPort.buscarPorNit(String.valueOf(id));
        }

        return empresaOpt.map(empresaExistente -> {
                    // 1. Actualizar datos básicos de identificación (si vienen en la peticion)
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

                    // 2. Actualizar datos de contacto y perfil
                    empresaExistente.setRazonSocial(empresaActualizada.getRazonSocial());
                    empresaExistente.setDireccion(empresaActualizada.getDireccion());
                    empresaExistente.setTelefono(empresaActualizada.getTelefono());
                    empresaExistente.setCorreo(empresaActualizada.getCorreo());
                    empresaExistente.setTamanoEmpresa(empresaActualizada.getTamanoEmpresa());
                    empresaExistente.setRepresentanteLegal(empresaActualizada.getRepresentanteLegal());

                    // 3. Gestionar Indicadores Financieros
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
                        // Recalcular indicadores automáticamente basados en los nuevos valores absolutos
                        this.recalcularIndicadores(empresaExistente.getIndicadores());
                    }

                    // 4. Gestionar Experiencias (Actualización de la lista)
                    if (empresaActualizada.getExperiencias() != null) {
                        // Limpiamos las experiencias actuales y añadimos las nuevas para simplificar la sincronización
                        // (En una app de producción esto debería ser más granular, pero para este caso asegura integridad)
                        empresaExistente.getExperiencias().clear();
                        empresaActualizada.getExperiencias().forEach(exp -> {
                            exp.setEmpresa(empresaExistente);
                            empresaExistente.getExperiencias().add(exp);
                        });
                    }

                    return empresaRepositoryPort.guardar(empresaExistente);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID o NIT: " + id));
    }
}
