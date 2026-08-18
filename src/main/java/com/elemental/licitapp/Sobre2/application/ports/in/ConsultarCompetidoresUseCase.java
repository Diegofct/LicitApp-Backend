package com.elemental.licitapp.Sobre2.application.ports.in;

import com.elemental.licitapp.Sobre2.domain.entity.ResumenCompetidor;

import java.util.List;

/** Inteligencia de competidores acumulada entre procesos. */
public interface ConsultarCompetidoresUseCase {

    /**
     * @param nombre filtro parcial por nombre del oferente; null o vacio devuelve todos
     */
    List<ResumenCompetidor> resumenPorCompetidor(String nombre);
}
