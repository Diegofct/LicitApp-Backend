package com.elemental.licitapp.CuadroDeObra.application.ports.in;

import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;

import java.util.Optional;

/**
 * Puerto público de solo lectura, expuesto a otros bounded contexts
 * (p. ej. AnalisisDeCumplimiento) que necesitan consultar requisitos
 * de licitación sin acoplarse al adaptador JPA interno.
 */
public interface ConsultarRequisitosUseCase {
    Optional<RequisitoLicitacion> obtenerPorCuadroId(Long cuadroId);
}
