package com.elemental.licitapp.Empresa.infrastructure.out.repository;

import com.elemental.licitapp.Empresa.application.ports.out.ExtraerRupPort;
import com.elemental.licitapp.InteligenciaArtificial.application.ports.in.ExtraerDatosRupUseCase;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccion;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida de Empresa que consume el puerto de entrada publico del slice
 * InteligenciaArtificial (no su implementacion). Analogo a {@code EmpresaModuloAdapter}
 * en AnalisisDeCumplimiento.
 */
@Component
public class InteligenciaArtificialModuloAdapter implements ExtraerRupPort {

    private final ExtraerDatosRupUseCase extraerDatosRup;

    public InteligenciaArtificialModuloAdapter(ExtraerDatosRupUseCase extraerDatosRup) {
        this.extraerDatosRup = extraerDatosRup;
    }

    @Override
    public ResultadoExtraccion extraer(byte[] pdf, String nombreArchivo) {
        return extraerDatosRup.extraer(pdf, nombreArchivo);
    }
}
