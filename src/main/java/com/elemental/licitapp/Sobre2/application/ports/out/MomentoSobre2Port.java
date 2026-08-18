package com.elemental.licitapp.Sobre2.application.ports.out;

import com.elemental.licitapp.Sobre2.domain.entity.MomentoSobre2;

/**
 * Frontera con CuadroDeObra y SeguimientoProceso para saber en que momento del proceso
 * esta el cuadro. Se implementa sobre sus puertos publicos de solo lectura, nunca sobre
 * sus repositorios JPA.
 */
public interface MomentoSobre2Port {

    /**
     * Nunca devuelve {@code null}: un cuadro sin seguimiento (todavia sin presentar, o
     * cargado como historico) es un caso normal y sale con ambas senales en {@code false}.
     */
    MomentoSobre2 obtener(Long cuadroDeObraId);
}
