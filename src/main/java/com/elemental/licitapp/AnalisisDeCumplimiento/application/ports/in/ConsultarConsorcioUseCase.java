package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;

import java.util.Optional;

public interface ConsultarConsorcioUseCase {
    Optional<ConformacionConsorcio> buscarPorCuadroDeObra(Long cuadroDeObraId);
}
