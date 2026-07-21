package com.elemental.licitapp.CuadroDeObra.application.ports.out;

import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.CuadroDeObra.domain.projection.CuadroDeObraRef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CuadroDeObraRepositoryPort {

    CuadroDeObra save(CuadroDeObra c);
    Optional<CuadroDeObra> findById(Long id);
    Page<CuadroDeObra> findByCuadroDeObraEstadoIn(List<CuadroDeObraEstado> estados, Pageable pageable);
    Map<CuadroDeObraEstado, Long> contarPorEstado();
    List<CuadroDeObraRef> obtenerReferencias();
    boolean existePorIdDelProceso(String idDelProceso);
    void delete(Long id);

}
