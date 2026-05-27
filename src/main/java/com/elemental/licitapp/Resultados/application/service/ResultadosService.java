package com.elemental.licitapp.Resultados.application.service;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.Resultados.application.ports.in.ConsultarHistorialResultadosUseCase;
import com.elemental.licitapp.Resultados.application.ports.in.ConsultarResumenResultadosUseCase;
import com.elemental.licitapp.Resultados.application.ports.out.ConsultarCuadrosPort;
import com.elemental.licitapp.Resultados.domain.entity.ItemHistorialResultado;
import com.elemental.licitapp.Resultados.domain.entity.ResumenResultados;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class ResultadosService
        implements ConsultarResumenResultadosUseCase, ConsultarHistorialResultadosUseCase {

    private static final List<CuadroDeObraEstado> ESTADOS_HISTORIAL = List.of(
            CuadroDeObraEstado.ADJUDICADO,
            CuadroDeObraEstado.NO_ADJUDICADO
    );
    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int ESCALA_TASA = 2;

    private final ConsultarCuadrosPort consultarCuadrosPort;

    public ResultadosService(ConsultarCuadrosPort consultarCuadrosPort) {
        this.consultarCuadrosPort = consultarCuadrosPort;
    }

    @Override
    @Transactional(readOnly = true)
    public ResumenResultados obtenerResumen() {
        Map<CuadroDeObraEstado, Long> conteo = consultarCuadrosPort.contarPorEstado();

        long porPresentar  = conteo.getOrDefault(CuadroDeObraEstado.POR_PRESENTAR,  0L);
        long presentados   = conteo.getOrDefault(CuadroDeObraEstado.PRESENTADO,     0L);
        long adjudicados   = conteo.getOrDefault(CuadroDeObraEstado.ADJUDICADO,     0L);
        long noAdjudicados = conteo.getOrDefault(CuadroDeObraEstado.NO_ADJUDICADO,  0L);
        long cancelados    = conteo.getOrDefault(CuadroDeObraEstado.CANCELADO,      0L);

        long procesosCerrados = adjudicados + noAdjudicados;
        long totalProcesos = porPresentar + presentados + adjudicados + noAdjudicados + cancelados;
        BigDecimal tasaExito = calcularTasaExito(adjudicados, procesosCerrados);

        return new ResumenResultados(
                totalProcesos,
                porPresentar,
                presentados,
                adjudicados,
                noAdjudicados,
                cancelados,
                procesosCerrados,
                tasaExito
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ItemHistorialResultado> obtenerHistorial(Pageable pageable) {
        return consultarCuadrosPort.listarHistorial(ESTADOS_HISTORIAL, pageable);
    }

    private BigDecimal calcularTasaExito(long adjudicados, long procesosCerrados) {
        if (procesosCerrados == 0L) {
            return BigDecimal.ZERO.setScale(ESCALA_TASA, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(adjudicados)
                .multiply(CIEN)
                .divide(BigDecimal.valueOf(procesosCerrados), ESCALA_TASA, RoundingMode.HALF_UP);
    }
}
