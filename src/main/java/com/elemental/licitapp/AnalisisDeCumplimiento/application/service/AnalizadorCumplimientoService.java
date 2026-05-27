package com.elemental.licitapp.AnalisisDeCumplimiento.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.DetalleRequisito;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.IntegranteEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.ResultadoEvaluacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.TipoParticipacion;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaExperiencia;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Empresa.domain.entity.CapacidadResidualProponente;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Empresa.domain.entity.IndicadoresFinancieros;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Service
public class AnalizadorCumplimientoService {

    private static final int ESCALA_RATIO = 4;
    private static final int ESCALA_MONEDA = 2;

    /**
     * Compara los indicadores YA CALCULADOS de la empresa con los requisitos del pliego.
     * No recalcula nada — eso es responsabilidad del módulo Empresa.
     */
    public ResultadoEvaluacion analizar(Empresa empresa, RequisitoLicitacion requisito, TipoParticipacion tipo) {
        List<DetalleRequisito> detalles = new ArrayList<>();
        IndicadoresFinancieros ind = empresa.getIndicadores();

        if (ind == null) {
            return new ResultadoEvaluacion(empresa.getId(), requisito.getCuadroDeObra().getId(), tipo, false, List.of(
                new DetalleRequisito("General", BigDecimal.ZERO, BigDecimal.ZERO, false, "La empresa no tiene indicadores financieros registrados.")
            ));
        }

        evaluarMayorIgual(detalles, "Índice de Liquidez", requisito.getLiquidez(), ind.getLiquidez());
        evaluarMenorIgual(detalles, "Nivel de Endeudamiento", requisito.getEndeudamiento(), ind.getEndeudamiento());
        evaluarMayorIgual(detalles, "Capital de Trabajo", requisito.getCapitalTrabajo(), ind.getCapitalTrabajo());
        evaluarMayorIgual(detalles, "Patrimonio Total", requisito.getPatrimonio(), ind.getPatrimonio());
        evaluarMayorIgual(detalles, "Cobertura de Intereses", requisito.getRazonCoberturaInteres(), ind.getRazonCoberturaInteres());
        evaluarMayorIgual(detalles, "ROE", requisito.getRentabilidadPatrimonio(), ind.getRentabilidadPatrimonio());
        evaluarMayorIgual(detalles, "ROA", requisito.getRentabilidadActivo(), ind.getRentabilidadActivo());

        Double valorObraSmmlv = requisito.getCuadroDeObra().getValorSMMLV();
        if (valorObraSmmlv != null && valorObraSmmlv > 0) {
            BigDecimal reqExp = BigDecimal.valueOf(valorObraSmmlv);
            Integer limiteContratos = requisito.getContrato() != null ? requisito.getContrato() : 5;

            ReglaExperiencia.ResultadoExperiencia resExp = ReglaExperiencia.validarExperiencia(
                    empresa.getExperiencias(), reqExp, esMipymeOMujer(empresa), limiteContratos);
            detalles.add(new DetalleRequisito("Experiencia Acumulada (SMMLV)", reqExp, resExp.totalSmmlv(), resExp.cumple(),
                resExp.cumple() ? "CUMPLE" : "NO CUMPLE"));
        }

        if (requisito.getKResidualProceso() != null) {
            BigDecimal reqK = BigDecimal.valueOf(requisito.getKResidualProceso());
            BigDecimal crp = obtenerUltimaK(empresa);
            boolean cumple = crp.compareTo(reqK) >= 0;
            detalles.add(new DetalleRequisito("Capacidad Residual (K)", reqK, crp, cumple,
                cumple ? "CUMPLE" : "NO CUMPLE: Capacidad insuficiente (" + crp + " vs " + reqK + ")"));
        }

        boolean cumpleGlobal = detalles.stream().allMatch(DetalleRequisito::cumple);
        return new ResultadoEvaluacion(empresa.getId(), requisito.getCuadroDeObra().getId(), tipo, cumpleGlobal, detalles);
    }

    /**
     * Evalúa un proponente plural (consorcio o UT) frente al pliego ponderando los
     * indicadores de cada integrante por su porcentaje real de participación.
     *
     * Reglas aplicadas (Decreto 1082/2015 y práctica estándar):
     *  - Ratios financieros (liquidez, endeudamiento, RCI, ROE, ROA): promedio ponderado
     *    Σᵢ ratioᵢ × %ᵢ.
     *  - Magnitudes acumulables (patrimonio, capital de trabajo, experiencia SMMLV,
     *    K residual): suma ponderada Σᵢ Xᵢ × %ᵢ.
     *
     * El porcentaje de cada integrante debe venir como fracción en (0,1]. Se asume
     * que el caller ya validó que la suma es 1 (lo hace ConformacionConsorcioAppService
     * para conformaciones reales; para la sugerencia 50/50 lo controla el llamador).
     */
    public ResultadoEvaluacion evaluarConsorcio(List<IntegranteEvaluacion> integrantes, RequisitoLicitacion requisito) {
        if (integrantes == null || integrantes.size() < 2) {
            throw new IllegalArgumentException("Un consorcio debe tener al menos 2 integrantes.");
        }

        Long primerEmpresaId = integrantes.get(0).empresa().getId();
        Long cuadroId = requisito.getCuadroDeObra().getId();

        for (IntegranteEvaluacion ie : integrantes) {
            if (ie.empresa().getIndicadores() == null) {
                return new ResultadoEvaluacion(primerEmpresaId, cuadroId, TipoParticipacion.CONSORCIO, false, List.of(
                    new DetalleRequisito("General", BigDecimal.ZERO, BigDecimal.ZERO, false,
                        "El integrante con empresaId=" + ie.empresa().getId() + " no tiene indicadores financieros registrados.")
                ));
            }
        }

        List<DetalleRequisito> detalles = new ArrayList<>();

        // Ratios → promedio ponderado.
        evaluarRatioPonderado(detalles, "Índice de Liquidez", requisito.getLiquidez(),
                integrantes, IndicadoresFinancieros::getLiquidez, true);
        evaluarRatioPonderado(detalles, "Nivel de Endeudamiento", requisito.getEndeudamiento(),
                integrantes, IndicadoresFinancieros::getEndeudamiento, false);
        evaluarRatioPonderado(detalles, "Cobertura de Intereses", requisito.getRazonCoberturaInteres(),
                integrantes, IndicadoresFinancieros::getRazonCoberturaInteres, true);
        evaluarRatioPonderado(detalles, "ROE", requisito.getRentabilidadPatrimonio(),
                integrantes, IndicadoresFinancieros::getRentabilidadPatrimonio, true);
        evaluarRatioPonderado(detalles, "ROA", requisito.getRentabilidadActivo(),
                integrantes, IndicadoresFinancieros::getRentabilidadActivo, true);

        // Magnitudes acumulables → suma ponderada.
        evaluarMagnitudPonderada(detalles, "Capital de Trabajo", requisito.getCapitalTrabajo(),
                integrantes, IndicadoresFinancieros::getCapitalTrabajo);
        evaluarMagnitudPonderada(detalles, "Patrimonio Total", requisito.getPatrimonio(),
                integrantes, IndicadoresFinancieros::getPatrimonio);

        // Experiencia consorciada: cada integrante aporta su experiencia × su %.
        Double valorObraSmmlv = requisito.getCuadroDeObra().getValorSMMLV();
        if (valorObraSmmlv != null && valorObraSmmlv > 0) {
            BigDecimal reqExp = BigDecimal.valueOf(valorObraSmmlv);
            Integer limiteContratos = requisito.getContrato() != null ? requisito.getContrato() : 5;
            BigDecimal expConsorciada = BigDecimal.ZERO;
            for (IntegranteEvaluacion ie : integrantes) {
                BigDecimal expIntegrante = ReglaExperiencia.validarExperiencia(
                        ie.empresa().getExperiencias(), BigDecimal.ZERO, esMipymeOMujer(ie.empresa()), limiteContratos
                ).totalSmmlv();
                expConsorciada = expConsorciada.add(expIntegrante.multiply(ie.porcentaje()));
            }
            expConsorciada = expConsorciada.setScale(ESCALA_RATIO, RoundingMode.HALF_UP);
            boolean cumple = expConsorciada.compareTo(reqExp) >= 0;
            detalles.add(new DetalleRequisito("Experiencia Acumulada (SMMLV)", reqExp, expConsorciada, cumple,
                cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // K residual consorciado: suma ponderada de la última K registrada por cada integrante.
        if (requisito.getKResidualProceso() != null) {
            BigDecimal reqK = BigDecimal.valueOf(requisito.getKResidualProceso());
            BigDecimal kConsorciado = BigDecimal.ZERO;
            for (IntegranteEvaluacion ie : integrantes) {
                kConsorciado = kConsorciado.add(obtenerUltimaK(ie.empresa()).multiply(ie.porcentaje()));
            }
            kConsorciado = kConsorciado.setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
            boolean cumple = kConsorciado.compareTo(reqK) >= 0;
            detalles.add(new DetalleRequisito("Capacidad Residual (K)", reqK, kConsorciado, cumple,
                cumple ? "CUMPLE" : "NO CUMPLE: Suma insuficiente (" + kConsorciado + ")"));
        }

        boolean cumpleGlobal = detalles.stream().allMatch(DetalleRequisito::cumple);
        return new ResultadoEvaluacion(primerEmpresaId, cuadroId, TipoParticipacion.CONSORCIO, cumpleGlobal, detalles);
    }

    private void evaluarMayorIgual(List<DetalleRequisito> detalles, String nombre, Double reqVal, BigDecimal actual) {
        if (reqVal == null) return;
        BigDecimal req = BigDecimal.valueOf(reqVal);
        BigDecimal actualSafe = actual != null ? actual : BigDecimal.ZERO;
        boolean cumple = actualSafe.compareTo(req) >= 0;
        detalles.add(new DetalleRequisito(nombre, req, actualSafe, cumple,
            cumple ? "CUMPLE" : "NO CUMPLE: Menor al requerido (" + req + ")"));
    }

    private void evaluarMenorIgual(List<DetalleRequisito> detalles, String nombre, Double reqVal, BigDecimal actual) {
        if (reqVal == null) return;
        BigDecimal req = BigDecimal.valueOf(reqVal);
        BigDecimal actualSafe = actual != null ? actual : BigDecimal.ZERO;
        boolean cumple = actualSafe.compareTo(req) <= 0;
        detalles.add(new DetalleRequisito(nombre, req, actualSafe, cumple,
            cumple ? "CUMPLE" : "NO CUMPLE: Supera el máximo (" + req + ")"));
    }

    private void evaluarRatioPonderado(List<DetalleRequisito> detalles, String nombre, Double reqVal,
                                       List<IntegranteEvaluacion> integrantes,
                                       Function<IndicadoresFinancieros, BigDecimal> extractor,
                                       boolean esMayorIgual) {
        if (reqVal == null) return;
        BigDecimal req = BigDecimal.valueOf(reqVal);
        BigDecimal actual = ponderar(integrantes, extractor).setScale(ESCALA_RATIO, RoundingMode.HALF_UP);
        boolean cumple = esMayorIgual ? actual.compareTo(req) >= 0 : actual.compareTo(req) <= 0;
        String mensaje = cumple
                ? "CUMPLE"
                : esMayorIgual ? "NO CUMPLE: Menor al requerido (" + req + ")"
                               : "NO CUMPLE: Supera el máximo (" + req + ")";
        detalles.add(new DetalleRequisito(nombre, req, actual, cumple, mensaje));
    }

    private void evaluarMagnitudPonderada(List<DetalleRequisito> detalles, String nombre, Double reqVal,
                                          List<IntegranteEvaluacion> integrantes,
                                          Function<IndicadoresFinancieros, BigDecimal> extractor) {
        if (reqVal == null) return;
        BigDecimal req = BigDecimal.valueOf(reqVal);
        BigDecimal actual = ponderar(integrantes, extractor).setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
        boolean cumple = actual.compareTo(req) >= 0;
        detalles.add(new DetalleRequisito(nombre, req, actual, cumple,
            cumple ? "CUMPLE" : "NO CUMPLE: Menor al requerido (" + req + ")"));
    }

    private BigDecimal ponderar(List<IntegranteEvaluacion> integrantes,
                                Function<IndicadoresFinancieros, BigDecimal> extractor) {
        BigDecimal acumulado = BigDecimal.ZERO;
        for (IntegranteEvaluacion ie : integrantes) {
            BigDecimal valor = extractor.apply(ie.empresa().getIndicadores());
            BigDecimal safe = valor != null ? valor : BigDecimal.ZERO;
            acumulado = acumulado.add(safe.multiply(ie.porcentaje()));
        }
        return acumulado;
    }

    private boolean esMipymeOMujer(Empresa empresa) {
        String tamano = empresa.getTamanoEmpresa() != null ? empresa.getTamanoEmpresa().toLowerCase() : "";
        return tamano.contains("micro") || tamano.contains("pequeña") || tamano.contains("mediana")
                || tamano.contains("mipyme") || tamano.contains("mujer");
    }

    private BigDecimal obtenerUltimaK(Empresa empresa) {
        if (empresa.getCapacidadesResiduales() == null || empresa.getCapacidadesResiduales().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return empresa.getCapacidadesResiduales().stream()
                .filter(c -> c.getResultadoK() != null)
                .reduce((first, second) -> second)
                .map(CapacidadResidualProponente::getResultadoK)
                .orElse(BigDecimal.ZERO);
    }
}
