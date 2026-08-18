package com.elemental.licitapp.CuadroDeObra.application.ports.in;

import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Puerto público de solo lectura, expuesto a otros bounded contexts
 * (p. ej. Resultados, Sobre2) que necesitan consultar cuadros y conteos por estado
 * sin acoplarse al adaptador JPA interno del módulo.
 */
public interface ConsultarCuadrosUseCase {
    Map<CuadroDeObraEstado, Long> contarPorEstado();
    Page<CuadroDeObra> listarPorEstados(List<CuadroDeObraEstado> estados, Pageable pageable);

    /**
     * Lectura puntual de un cuadro. Devuelve Optional (y no lanza como
     * {@code findCuadroById}) porque los consumidores externos deciden cómo
     * reportar la ausencia dentro de su propio flujo.
     */
    Optional<CuadroDeObra> buscarPorId(Long id);
}
