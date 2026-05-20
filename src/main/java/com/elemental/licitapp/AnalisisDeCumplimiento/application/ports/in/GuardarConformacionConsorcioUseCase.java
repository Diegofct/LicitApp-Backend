package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;

public interface GuardarConformacionConsorcioUseCase {
    ConformacionConsorcio guardar(ConformacionConsorcio conformacion);
}
