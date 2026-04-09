package com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.out;

import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import java.util.Optional;

public interface ObtenerRequisitosPort {
    Optional<RequisitoLicitacion> obtenerPorCuadroId(Long cuadroId);
}
