package com.elemental.licitapp.Resultados.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.application.ports.in.ConsultarCuadrosUseCase;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.Resultados.application.ports.out.ConsultarCuadrosPort;
import com.elemental.licitapp.Resultados.domain.entity.ItemHistorialResultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class ConsultarCuadrosAdapter implements ConsultarCuadrosPort {

    private final ConsultarCuadrosUseCase consultarCuadros;

    public ConsultarCuadrosAdapter(ConsultarCuadrosUseCase consultarCuadros) {
        this.consultarCuadros = consultarCuadros;
    }

    @Override
    public Map<CuadroDeObraEstado, Long> contarPorEstado() {
        return consultarCuadros.contarPorEstado();
    }

    @Override
    public Page<ItemHistorialResultado> listarHistorial(List<CuadroDeObraEstado> estados, Pageable pageable) {
        return consultarCuadros.listarPorEstados(estados, pageable).map(this::toDomain);
    }

    private ItemHistorialResultado toDomain(CuadroDeObra c) {
        return new ItemHistorialResultado(
                c.getId(),
                c.getNumeroProceso(),
                c.getEntidadContratante(),
                c.getDescripcionObjeto(),
                c.getMonto(),
                c.getCuadroDeObraEstado(),
                c.getObservacion(),
                c.getFechaPublicacion(),
                c.getFechaCierre()
        );
    }
}
