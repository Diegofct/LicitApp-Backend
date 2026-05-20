package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;

import java.util.Optional;

public interface ConformacionConsorcioRepositoryPort {
    ConformacionConsorcio guardar(ConformacionConsorcio conformacion);
    Optional<ConformacionConsorcio> buscarPorCuadroDeObra(Long cuadroDeObraId);
}
