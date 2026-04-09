package com.elemental.licitapp.Empresa.application.ports.in;

import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import java.util.List;
import java.util.Optional;

public interface EmpresaUseCase {
    Empresa crearEmpresa(Empresa empresa);
    List<Empresa> obtenerTodas();
    Optional<Empresa> obtenerPorNit(String nit);
    Optional<Empresa> obtenerPorId(Long id);
    void eliminarEmpresa(Long id);
    Empresa actualizarEmpresa(Long id, Empresa empresa);
}
