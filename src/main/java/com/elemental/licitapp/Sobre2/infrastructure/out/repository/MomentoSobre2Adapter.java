package com.elemental.licitapp.Sobre2.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.application.ports.in.ConsultarCuadrosUseCase;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.SeguimientoProceso.application.ports.in.ConsultarSeguimientoUseCase;
import com.elemental.licitapp.SeguimientoProceso.domain.entity.EventoSeguimiento;
import com.elemental.licitapp.SeguimientoProceso.domain.entity.SeguimientoProceso;
import com.elemental.licitapp.SeguimientoProceso.domain.enums.TipoEventoSeguimiento;
import com.elemental.licitapp.Sobre2.application.ports.out.MomentoSobre2Port;
import com.elemental.licitapp.Sobre2.domain.entity.MomentoSobre2;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Traduce el estado del cuadro y los eventos de seguimiento a las dos senales que le
 * importan al Sobre 2. Consume los puertos publicos de solo lectura de CuadroDeObra y
 * SeguimientoProceso, nunca sus repositorios JPA.
 *
 * <p>Nombre de clase deliberadamente distinto de {@code SeguimientoModuloAdapter} (que ya
 * existe en CuadroDeObra): Spring nombra los beans por el nombre simple de la clase y dos
 * homonimos en paquetes distintos revientan el arranque con
 * {@code ConflictingBeanDefinitionException}.</p>
 */
@Component
public class MomentoSobre2Adapter implements MomentoSobre2Port {

    private final ConsultarCuadrosUseCase consultarCuadros;
    private final ConsultarSeguimientoUseCase consultarSeguimiento;

    public MomentoSobre2Adapter(ConsultarCuadrosUseCase consultarCuadros,
                                ConsultarSeguimientoUseCase consultarSeguimiento) {
        this.consultarCuadros = consultarCuadros;
        this.consultarSeguimiento = consultarSeguimiento;
    }

    @Override
    public MomentoSobre2 obtener(Long cuadroDeObraId) {
        String estado = consultarCuadros.buscarPorId(cuadroDeObraId)
                .map(CuadroDeObra::getCuadroDeObraEstado)
                .map(CuadroDeObraEstado::name)
                .orElse(null);

        // Sin seguimiento no hay evidencia de avance: ambas senales quedan en false. No es
        // un error, es lo normal en un cuadro por presentar o cargado como historico.
        List<EventoSeguimiento> eventos = consultarSeguimiento.obtenerPorCuadroDeObra(cuadroDeObraId)
                .map(SeguimientoProceso::getEventos)
                .filter(Objects::nonNull)
                .orElseGet(List::of);

        return new MomentoSobre2(
                estado,
                hayEvento(eventos, TipoEventoSeguimiento.INFORME_EVALUACION_DEFINITIVO),
                hayEvento(eventos, TipoEventoSeguimiento.AUDIENCIA_REALIZADA));
    }

    private static boolean hayEvento(List<EventoSeguimiento> eventos, TipoEventoSeguimiento tipo) {
        return eventos.stream().anyMatch(e -> e.getTipo() == tipo);
    }
}
