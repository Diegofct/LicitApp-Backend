package com.elemental.licitapp.Licitaciones.application.ports.out;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SecopApiPort {
    Page<Licitacion> obtenerLicitacionesPorModalidad(String modalidad, Pageable pageable);
}
