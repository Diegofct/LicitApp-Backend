package com.elemental.licitapp.Licitaciones.application.ports.out;

import com.elemental.licitapp.Licitaciones.domain.entity.Licitacion;

import java.util.List;

public interface SecopApiPort {
    List<Licitacion> obtenerProcesosActivos(int pageNumber, int pageSize);
}
