package com.elemental.licitapp.Empresa.application.ports.in;

import com.elemental.licitapp.Empresa.domain.entity.Empresa;

import java.util.List;
import java.util.Optional;

/**
 * Puerto público de solo lectura, expuesto a otros bounded contexts
 * (p. ej. AnalisisDeCumplimiento) que necesitan consumir empresas
 * sin acoplarse al adaptador JPA interno.
 */
public interface ConsultarEmpresasUseCase {
    Optional<Empresa> obtenerPorId(Long id);
    List<Empresa> obtenerTodas();
}
