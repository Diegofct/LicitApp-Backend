package com.elemental.licitapp.CuadroDeObra.application.ports.out;

import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CuadroDeObraRepositoryPort {

    CuadroDeObra save(CuadroDeObra c);
    Optional<CuadroDeObra> findById(Long id);
    List<CuadroDeObra> findAll();
    Page<CuadroDeObra> findByCuadroDeObraEstadoIn(List<CuadroDeObraEstado> estados, Pageable pageable);
    void delete(Long id);

}
