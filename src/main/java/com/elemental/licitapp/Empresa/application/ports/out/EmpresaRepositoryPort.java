package com.elemental.licitapp.Empresa.application.ports.out;

import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import java.util.List;
import java.util.Optional;

public interface EmpresaRepositoryPort {
    Empresa guardar(Empresa empresa);
    Optional<Empresa> buscarPorId(Long id);
    Optional<Empresa> buscarPorNit(String nit);
    List<Empresa> listarTodas();
    void eliminar(Long id);
}
