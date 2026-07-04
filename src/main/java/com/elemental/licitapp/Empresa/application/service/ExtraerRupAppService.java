package com.elemental.licitapp.Empresa.application.service;

import com.elemental.licitapp.Empresa.application.ports.in.ExtraerRupUseCase;
import com.elemental.licitapp.Empresa.application.ports.out.ExtraerRupPort;
import com.elemental.licitapp.InteligenciaArtificial.domain.entity.ResultadoExtraccion;
import org.springframework.stereotype.Service;

/**
 * Pass-through fino: el slice IA ya valida y orquesta; Empresa solo entrega el PDF y
 * recibe el borrador. Existe para que el controller dependa de un puerto de entrada y no
 * de uno de salida.
 */
@Service
public class ExtraerRupAppService implements ExtraerRupUseCase {

    private final ExtraerRupPort extraerRupPort;

    public ExtraerRupAppService(ExtraerRupPort extraerRupPort) {
        this.extraerRupPort = extraerRupPort;
    }

    @Override
    public ResultadoExtraccion extraerDatosRup(byte[] pdf, String nombreArchivo) {
        return extraerRupPort.extraer(pdf, nombreArchivo);
    }
}
