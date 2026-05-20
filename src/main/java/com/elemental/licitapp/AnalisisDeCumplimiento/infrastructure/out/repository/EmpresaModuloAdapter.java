package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.out.repository;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerEmpresasPort;
import com.elemental.licitapp.Empresa.application.ports.in.ConsultarEmpresasUseCase;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EmpresaModuloAdapter implements ObtenerEmpresasPort {

    private final ConsultarEmpresasUseCase consultarEmpresas;

    public EmpresaModuloAdapter(ConsultarEmpresasUseCase consultarEmpresas) {
        this.consultarEmpresas = consultarEmpresas;
    }

    @Override
    public Optional<Empresa> obtenerPorId(Long id) {
        return consultarEmpresas.obtenerPorId(id);
    }

    @Override
    public List<Empresa> obtenerTodas() {
        return consultarEmpresas.obtenerTodas();
    }
}
