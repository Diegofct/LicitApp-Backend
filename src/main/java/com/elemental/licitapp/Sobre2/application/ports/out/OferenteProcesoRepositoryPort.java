package com.elemental.licitapp.Sobre2.application.ports.out;

import com.elemental.licitapp.Sobre2.domain.entity.OferenteProceso;
import com.elemental.licitapp.Sobre2.domain.entity.ResumenCompetidor;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OferenteProcesoRepositoryPort {

    List<OferenteProceso> findByCuadroId(Long cuadroId);

    Optional<OferenteProceso> findById(Long id);

    Optional<OferenteProceso> findByCuadroIdEIdentificador(Long cuadroId, String identificadorOferta);

    OferenteProceso save(OferenteProceso oferente);

    List<OferenteProceso> saveAll(List<OferenteProceso> oferentes);

    void deleteById(Long id);

    /** Borrado en lote; se usa para purgar las filas de SECOP que quedaron obsoletas. */
    void deleteAllById(Collection<Long> ids);

    List<ResumenCompetidor> resumenPorCompetidor(String nombre);
}
