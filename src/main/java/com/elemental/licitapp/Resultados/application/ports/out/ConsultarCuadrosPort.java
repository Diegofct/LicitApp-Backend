package com.elemental.licitapp.Resultados.application.ports.out;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.Resultados.domain.entity.ItemHistorialResultado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ConsultarCuadrosPort {
    Map<CuadroDeObraEstado, Long> contarPorEstado();
    Page<ItemHistorialResultado> listarHistorial(List<CuadroDeObraEstado> estados, Pageable pageable);
}
