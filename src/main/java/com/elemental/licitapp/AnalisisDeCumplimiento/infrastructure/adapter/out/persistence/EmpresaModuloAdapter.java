package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.out.persistence;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.out.ObtenerEmpresasPort;
import com.elemental.licitapp.Empresa.application.ports.in.ConsultarEmpresasUseCase;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmpresaModuloAdapter implements ObtenerEmpresasPort {

    private final ConsultarEmpresasUseCase consultarEmpresas;

    @Override
    public Optional<Empresa> obtenerPorId(Long id) {
        return consultarEmpresas.obtenerPorId(id);
    }

    @Override
    public List<Empresa> obtenerTodas() {
        return consultarEmpresas.obtenerTodas();
    }
}
