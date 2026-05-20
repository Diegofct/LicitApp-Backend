package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.out.repository;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerRequisitosPort;
import com.elemental.licitapp.CuadroDeObra.application.ports.in.ConsultarRequisitosUseCase;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CuadroDeObraModuloAdapter implements ObtenerRequisitosPort {

    private final ConsultarRequisitosUseCase consultarRequisitos;

    public CuadroDeObraModuloAdapter(ConsultarRequisitosUseCase consultarRequisitos) {
        this.consultarRequisitos = consultarRequisitos;
    }

    @Override
    public Optional<RequisitoLicitacion> obtenerPorCuadroId(Long cuadroId) {
        return consultarRequisitos.obtenerPorCuadroId(cuadroId);
    }
}
