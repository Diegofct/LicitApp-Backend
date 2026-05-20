package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.ConsultarConsorcioUseCase;
import com.elemental.licitapp.CuadroDeObra.application.ports.out.ExisteConformacionConsorcioPort;
import org.springframework.stereotype.Component;

@Component
public class ConformacionConsorcioModuloAdapter implements ExisteConformacionConsorcioPort {

    private final ConsultarConsorcioUseCase consultarConsorcioUseCase;

    public ConformacionConsorcioModuloAdapter(ConsultarConsorcioUseCase consultarConsorcioUseCase) {
        this.consultarConsorcioUseCase = consultarConsorcioUseCase;
    }

    @Override
    public boolean existePara(Long cuadroDeObraId) {
        return consultarConsorcioUseCase.buscarPorCuadroDeObra(cuadroDeObraId).isPresent();
    }
}
