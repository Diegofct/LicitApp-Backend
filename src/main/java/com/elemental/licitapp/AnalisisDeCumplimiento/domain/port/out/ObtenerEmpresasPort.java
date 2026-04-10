package com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.out;

import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import java.util.Optional;
import java.util.List;

public interface ObtenerEmpresasPort {
    Optional<Empresa> obtenerPorId(Long id);
    List<Empresa> obtenerTodas();
}
