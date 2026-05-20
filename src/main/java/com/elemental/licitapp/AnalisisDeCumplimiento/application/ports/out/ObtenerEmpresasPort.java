package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out;

import com.elemental.licitapp.Empresa.domain.entity.Empresa;

import java.util.List;
import java.util.Optional;

public interface ObtenerEmpresasPort {
    Optional<Empresa> obtenerPorId(Long id);
    List<Empresa> obtenerTodas();
}
