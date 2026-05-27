package com.elemental.licitapp.AnalisisDeCumplimiento.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.ConsultarConsorcioUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.EvaluarConformacionUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerEmpresasPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerRequisitosPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ConformacionConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.IntegranteConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.IntegranteEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class EvaluarConformacionAppService implements EvaluarConformacionUseCase {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final int ESCALA_FRACCION = 6;

    private final ConsultarConsorcioUseCase consultarConsorcio;
    private final ObtenerEmpresasPort obtenerEmpresas;
    private final ObtenerRequisitosPort obtenerRequisitos;
    private final AnalizadorCumplimientoService analizador;

    public EvaluarConformacionAppService(ConsultarConsorcioUseCase consultarConsorcio,
                                         ObtenerEmpresasPort obtenerEmpresas,
                                         ObtenerRequisitosPort obtenerRequisitos,
                                         AnalizadorCumplimientoService analizador) {
        this.consultarConsorcio = consultarConsorcio;
        this.obtenerEmpresas = obtenerEmpresas;
        this.obtenerRequisitos = obtenerRequisitos;
        this.analizador = analizador;
    }

    @Override
    @Transactional(readOnly = true)
    public ResultadoEvaluacion evaluarConformacion(Long cuadroDeObraId) {
        ConformacionConsorcio conformacion = consultarConsorcio.buscarPorCuadroDeObra(cuadroDeObraId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No existe conformación para el cuadro " + cuadroDeObraId
                                + ". Registra una en POST /analisis/consorcios antes de evaluarla."));

        RequisitoLicitacion requisito = obtenerRequisitos.obtenerPorCuadroId(cuadroDeObraId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontraron requisitos para el cuadro " + cuadroDeObraId));

        if (conformacion.getTipoParticipacion() == TipoParticipacion.INDIVIDUAL) {
            IntegranteConsorcio unico = conformacion.getIntegrantes().get(0);
            Empresa empresa = obtenerEmpresas.obtenerPorId(unico.getEmpresaId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Empresa no encontrada con ID: " + unico.getEmpresaId()));
            return analizador.analizar(empresa, requisito, TipoParticipacion.INDIVIDUAL);
        }

        List<IntegranteEvaluacion> integrantes = conformacion.getIntegrantes().stream()
                .map(i -> {
                    Empresa empresa = obtenerEmpresas.obtenerPorId(i.getEmpresaId())
                            .orElseThrow(() -> new ResourceNotFoundException(
                                    "Empresa no encontrada con ID: " + i.getEmpresaId()));
                    BigDecimal fraccion = i.getPorcentajeParticipacion()
                            .divide(CIEN, ESCALA_FRACCION, RoundingMode.HALF_UP);
                    return new IntegranteEvaluacion(empresa, fraccion);
                })
                .toList();

        ResultadoEvaluacion resultado = analizador.evaluarConsorcio(integrantes, requisito);

        // Preservar el tipo real (CONSORCIO vs UNION_TEMPORAL); el analizador siempre devuelve CONSORCIO.
        return new ResultadoEvaluacion(
                resultado.empresaId(),
                resultado.cuadroId(),
                conformacion.getTipoParticipacion(),
                resultado.cumpleGlobal(),
                resultado.detalles()
        );
    }
}
