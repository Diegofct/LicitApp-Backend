package com.elemental.licitapp.CuadroDeObra.application.ports.in;

import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.CuadroDeObra.domain.enums.PresentacionMarca;
import com.elemental.licitapp.CuadroDeObra.domain.projection.CuadroDeObraRef;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CuadroDeObraUseCase {

    CuadroDeObra createCuadro(CuadroDeObra nuevoCuadro);
    CuadroDeObra updateCuadro(Long id, CuadroDeObra cuadroConActualizaciones);
    CuadroDeObra updateEstado(Long id, CuadroDeObraEstado nuevoEstado);
    CuadroDeObra actualizarPresentacion(Long id, PresentacionMarca marca);
    void deleteCuadro(Long id);
    CuadroDeObra findCuadroById(Long id);
    Page<CuadroDeObra> findCuadrosPorVistas(String vista, Pageable pageable);
    List<CuadroDeObraRef> obtenerReferencias();

    RequisitoLicitacion saveRequisito(Long cuadroId, RequisitoLicitacion requisito);
    RequisitoLicitacion actualizarRequisito(Long cuadroId, RequisitoLicitacion parche);
    RequisitoLicitacion getRequisitoByCuadroId(Long cuadroId);
}
