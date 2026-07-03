package com.elemental.licitapp.CuadroDeObra.infrastructure.out.repository;

import com.elemental.licitapp.CuadroDeObra.application.ports.out.ExtraerPliegoPort;
import com.elemental.licitapp.InteligenciaArtificial.application.ports.in.ExtraerRequisitosPliegoUseCase;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccionPliego;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida hacia el slice InteligenciaArtificial: consume su puerto de entrada
 * publico, no su repositorio ni el proveedor LLM. Mismo patron que
 * {@code EmpresaModuloAdapter} consumiendo {@code ConsultarEmpresasUseCase}.
 *
 * <p>Nombre de bean explicito porque Empresa tiene otro adaptador con el mismo nombre simple
 * de clase; sin esto, el escaneo de componentes genera un conflicto de nombre de bean.
 */
@Component("inteligenciaArtificialPliegoAdapter")
public class InteligenciaArtificialModuloAdapter implements ExtraerPliegoPort {

    private final ExtraerRequisitosPliegoUseCase extraerRequisitosPliego;

    public InteligenciaArtificialModuloAdapter(ExtraerRequisitosPliegoUseCase extraerRequisitosPliego) {
        this.extraerRequisitosPliego = extraerRequisitosPliego;
    }

    @Override
    public ResultadoExtraccionPliego extraer(byte[] pdfEnsamblado, String nombreArchivo) {
        return extraerRequisitosPliego.extraer(pdfEnsamblado, nombreArchivo);
    }
}
