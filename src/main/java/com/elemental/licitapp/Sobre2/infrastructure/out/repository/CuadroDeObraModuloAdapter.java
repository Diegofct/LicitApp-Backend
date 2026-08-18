package com.elemental.licitapp.Sobre2.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.application.ports.in.ConsultarCuadrosUseCase;
import com.elemental.licitapp.CuadroDeObra.application.ports.in.ConsultarRequisitosUseCase;
import com.elemental.licitapp.CuadroDeObra.domain.entity.CuadroDeObra;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Sobre2.application.ports.out.DatosProcesoPort;
import com.elemental.licitapp.Sobre2.domain.entity.DatosProceso;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Frontera con CuadroDeObra. Consume sus puertos publicos de solo lectura y nunca su
 * repositorio JPA, igual que hacen los adaptadores de Resultados y AnalisisDeCumplimiento.
 */
// Nombre de bean explicito: AnalisisDeCumplimiento ya tiene una clase con este mismo nombre
// simple y Spring los nombra por clase, asi que sin esto el contexto no arranca
// (ConflictingBeanDefinitionException). Mismo patron que "inteligenciaArtificialPliegoAdapter".
@Component("cuadroDeObraDatosProcesoAdapter")
public class CuadroDeObraModuloAdapter implements DatosProcesoPort {

    private final ConsultarCuadrosUseCase consultarCuadros;
    private final ConsultarRequisitosUseCase consultarRequisitos;

    public CuadroDeObraModuloAdapter(ConsultarCuadrosUseCase consultarCuadros,
                                     ConsultarRequisitosUseCase consultarRequisitos) {
        this.consultarCuadros = consultarCuadros;
        this.consultarRequisitos = consultarRequisitos;
    }

    @Override
    public Optional<DatosProceso> obtener(Long cuadroId) {
        return consultarCuadros.buscarPorId(cuadroId)
                .map(cuadro -> new DatosProceso(
                        cuadro.getId(),
                        cuadro.getIdDelProceso(),
                        cuadro.getNumeroProceso(),
                        cuadro.getEntidadContratante(),
                        resolverPresupuestoOficial(cuadroId, cuadro)));
    }

    /**
     * El presupuesto oficial que manda es el capturado del <b>pliego definitivo</b> en los
     * requisitos: el {@code monto} del cuadro viene del {@code precio_base} que SECOP publica
     * con el proyecto de pliego y puede haber cambiado por adenda. Si no hay requisitos
     * cargados todavia, se cae al monto para no dejar el analisis sin base de comparacion.
     */
    private BigDecimal resolverPresupuestoOficial(Long cuadroId, CuadroDeObra cuadro) {
        BigDecimal delPliego = consultarRequisitos.obtenerPorCuadroId(cuadroId)
                .map(RequisitoLicitacion::getPresupuesto)
                .orElse(null);
        if (delPliego != null && delPliego.signum() > 0) {
            return delPliego;
        }
        return cuadro.getMonto();
    }
}
