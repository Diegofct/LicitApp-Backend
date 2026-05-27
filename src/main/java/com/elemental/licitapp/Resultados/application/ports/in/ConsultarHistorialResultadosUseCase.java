package com.elemental.licitapp.Resultados.application.ports.in;

import com.elemental.licitapp.Resultados.domain.entity.ItemHistorialResultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ConsultarHistorialResultadosUseCase {
    Page<ItemHistorialResultado> obtenerHistorial(Pageable pageable);
}
