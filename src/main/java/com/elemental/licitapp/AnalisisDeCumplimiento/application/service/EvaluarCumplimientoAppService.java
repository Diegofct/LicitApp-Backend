package com.elemental.licitapp.AnalisisDeCumplimiento.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.EvaluarCumplimientoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerEmpresasPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerRequisitosPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.PropuestaConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class EvaluarCumplimientoAppService implements EvaluarCumplimientoUseCase {

    private static final BigDecimal PORCENTAJE_SIMULACION_DEFAULT = new BigDecimal("0.5");

    private final ObtenerEmpresasPort obtenerEmpresasPort;
    private final ObtenerRequisitosPort obtenerRequisitosPort;
    private final AnalizadorCumplimientoService analizadorService;
    private final BuscadorConsorcioService buscadorConsorcioService;
    private final int maxPropuestas;

    public EvaluarCumplimientoAppService(ObtenerEmpresasPort obtenerEmpresasPort,
                                         ObtenerRequisitosPort obtenerRequisitosPort,
                                         AnalizadorCumplimientoService analizadorService,
                                         BuscadorConsorcioService buscadorConsorcioService,
                                         @Value("${analisis.sugerencia.max-propuestas:3}") int maxPropuestas) {
        this.obtenerEmpresasPort = obtenerEmpresasPort;
        this.obtenerRequisitosPort = obtenerRequisitosPort;
        this.analizadorService = analizadorService;
        this.buscadorConsorcioService = buscadorConsorcioService;
        this.maxPropuestas = maxPropuestas;
    }

    @Override
    public ResultadoEvaluacion evaluar(Long empresaId, Long cuadroId, TipoParticipacion tipo,
                                       BigDecimal porcentajeSimulacion) {
        Empresa empresa = obtenerEmpresasPort.obtenerPorId(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + empresaId));

        RequisitoLicitacion requisito = obtenerRequisitosPort.obtenerPorCuadroId(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron requisitos para el cuadro de obra: " + cuadroId));

        ResultadoEvaluacion resultadoIndividual = analizadorService.analizar(empresa, requisito, tipo);

        if (resultadoIndividual.cumpleGlobal()) {
            return resultadoIndividual;
        }

        BigDecimal porcentajeSolicitante = porcentajeSimulacion != null
                ? porcentajeSimulacion
                : PORCENTAJE_SIMULACION_DEFAULT;

        List<Empresa> todasLasEmpresas = obtenerEmpresasPort.obtenerTodas();

        List<PropuestaConsorcio> sugerencias = buscadorConsorcioService.buscar(
                empresa, todasLasEmpresas, requisito, porcentajeSolicitante, maxPropuestas);

        return new ResultadoEvaluacion(
            resultadoIndividual.empresaId(),
            resultadoIndividual.cuadroId(),
            resultadoIndividual.tipo(),
            resultadoIndividual.cumpleGlobal(),
            resultadoIndividual.detalles(),
            sugerencias
        );
    }
}
