package com.elemental.licitapp.AnalisisDeCumplimiento.application;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.*;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.in.EvaluarCumplimientoUseCase;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.port.out.*;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.AnalizadorCumplimientoService;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EvaluarCumplimientoAppService implements EvaluarCumplimientoUseCase {

    private final ObtenerEmpresasPort obtenerEmpresasPort;
    private final ObtenerRequisitosPort obtenerRequisitosPort;
    private final AnalizadorCumplimientoService analizadorService;

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

        // Si no cumple, buscar posibles consorcios
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
