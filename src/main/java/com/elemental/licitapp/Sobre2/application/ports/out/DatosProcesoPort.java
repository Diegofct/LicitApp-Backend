package com.elemental.licitapp.Sobre2.application.ports.out;

import com.elemental.licitapp.Sobre2.domain.entity.DatosProceso;

import java.util.Optional;

/**
 * Frontera con CuadroDeObra. Se implementa sobre sus puertos publicos de solo lectura,
 * nunca sobre su repositorio JPA.
 */
public interface DatosProcesoPort {

    Optional<DatosProceso> obtener(Long cuadroId);
}
