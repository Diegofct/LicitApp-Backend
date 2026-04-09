package com.elemental.licitapp.AnalisisDeCumplimiento.infrastructure.adapter.out.persistence;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.out.ObtenerRequisitosPort;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository.RequisitoLicitacionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CuadroDeObraModuloAdapter implements ObtenerRequisitosPort {
    private final RequisitoLicitacionJpaRepository repository;

    @Override
    public Optional<RequisitoLicitacion> obtenerPorCuadroId(Long cuadroId) {
        return repository.findByCuadroDeObraId(cuadroId);
    }
}
