package com.elemental.licitapp.CuadroDeObra.application.ports.in;

import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

/**
 * Puerto público de solo lectura, expuesto a otros bounded contexts
 * (p. ej. AnalisisDeCumplimiento) que necesitan consultar requisitos
 * de licitación sin acoplarse al adaptador JPA interno.
 */
public interface ConsultarRequisitosUseCase {
    Optional<RequisitoLicitacion> obtenerPorCuadroId(Long cuadroId);

    /** (RF3) Subconjunto de {@code ids} cuyos cuadros ya tienen requisitos guardados. */
    Set<Long> cuadrosConRequisitos(Collection<Long> ids);
}
