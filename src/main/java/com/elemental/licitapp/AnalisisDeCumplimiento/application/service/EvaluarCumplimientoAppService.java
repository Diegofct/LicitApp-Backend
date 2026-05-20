package com.elemental.licitapp.AnalisisDeCumplimiento.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.in.EvaluarCumplimientoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerEmpresasPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.application.ports.out.ObtenerRequisitosPort;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.SugerenciaConsorcio;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EvaluarCumplimientoAppService implements EvaluarCumplimientoUseCase {

    private final ObtenerEmpresasPort obtenerEmpresasPort;
    private final ObtenerRequisitosPort obtenerRequisitosPort;
    private final AnalizadorCumplimientoService analizadorService;

    public EvaluarCumplimientoAppService(ObtenerEmpresasPort obtenerEmpresasPort,
                                         ObtenerRequisitosPort obtenerRequisitosPort,
                                         AnalizadorCumplimientoService analizadorService) {
        this.obtenerEmpresasPort = obtenerEmpresasPort;
        this.obtenerRequisitosPort = obtenerRequisitosPort;
        this.analizadorService = analizadorService;
    }

    @Override
    public ResultadoEvaluacion evaluar(Long empresaId, Long cuadroId, TipoParticipacion tipo) {
        Empresa empresa = obtenerEmpresasPort.obtenerPorId(empresaId)
                .orElseThrow(() -> new ResourceNotFoundException("Empresa no encontrada con ID: " + empresaId));

        RequisitoLicitacion requisito = obtenerRequisitosPort.obtenerPorCuadroId(cuadroId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontraron requisitos para el cuadro de obra: " + cuadroId));

        ResultadoEvaluacion resultadoIndividual = analizadorService.analizar(empresa, requisito, tipo);

        if (resultadoIndividual.cumpleGlobal()) {
            return resultadoIndividual;
        }

        List<SugerenciaConsorcio> sugerencias = new ArrayList<>();
        List<Empresa> todasLasEmpresas = obtenerEmpresasPort.obtenerTodas();

        for (Empresa otraEmpresa : todasLasEmpresas) {
            if (otraEmpresa.getId().equals(empresa.getId())) {
                continue;
            }

            ResultadoEvaluacion resultadoConsorcio = analizadorService.evaluarConsorcio(empresa, otraEmpresa, requisito);

            if (resultadoConsorcio.cumpleGlobal()) {
                sugerencias.add(new SugerenciaConsorcio(
                    otraEmpresa.getId(),
                    otraEmpresa.getNit(),
                    otraEmpresa.getRazonSocial()
                ));
            }
        }

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
