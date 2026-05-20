package com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out;

import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;

import java.util.Optional;

public interface ObtenerRequisitosPort {
    Optional<RequisitoLicitacion> obtenerPorCuadroId(Long cuadroId);
}
