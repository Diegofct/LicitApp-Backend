package com.elemental.licitapp.CuadroDeObra.application.ports.in;

import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CuadroDeObraUseCase {

    CuadroDeObra createCuadro(CuadroDeObra nuevoCuadro);
    CuadroDeObra updateCuadro(Long id, CuadroDeObra cuadroConActualizaciones);
    CuadroDeObra updateEstado(Long id, CuadroDeObraEstado nuevoEstado);
    void deleteCuadro(Long id);
    CuadroDeObra findCuadroById(Long id);
    Page<CuadroDeObra> findCuadrosPorVistas(String vista, Pageable pageable);

    RequisitoLicitacion saveRequisito(Long cuadroId, RequisitoLicitacion requisito);
    RequisitoLicitacion actualizarRequisito(Long cuadroId, RequisitoLicitacion parche);
    RequisitoLicitacion getRequisitoByCuadroId(Long cuadroId);
}
