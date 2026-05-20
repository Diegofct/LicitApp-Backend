package com.elemental.licitapp.AnalisisDeCumplimiento.application.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity.DetalleRequisito;
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

@Service
public class AnalizadorCumplimientoService {

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
            String tamano = empresa.getTamanoEmpresa() != null ? empresa.getTamanoEmpresa().toLowerCase() : "";
            boolean esMipymeOMujer = tamano.contains("micro") || tamano.contains("pequeña") || tamano.contains("mediana") || tamano.contains("mipyme") || tamano.contains("mujer");
            Integer limiteContratos = requisito.getContrato() != null ? requisito.getContrato() : 5;

            ReglaExperiencia.ResultadoExperiencia resExp = ReglaExperiencia.validarExperiencia(empresa.getExperiencias(), reqExp, esMipymeOMujer, limiteContratos);
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

    public ResultadoEvaluacion evaluarConsorcio(Empresa a, Empresa b, RequisitoLicitacion requisito) {
        List<DetalleRequisito> detalles = new ArrayList<>();
        IndicadoresFinancieros indA = a.getIndicadores();
        IndicadoresFinancieros indB = b.getIndicadores();

        if (indA == null || indB == null) {
            return new ResultadoEvaluacion(a.getId(), requisito.getCuadroDeObra().getId(), TipoParticipacion.CONSORCIO, false, List.of(
                new DetalleRequisito("General", BigDecimal.ZERO, BigDecimal.ZERO, false, "Indicadores incompletos.")
            ));
        }

        BigDecimal participacion = new BigDecimal("0.5");

        evaluarFinancieroConsorcio(detalles, "Índice de Liquidez", requisito.getLiquidez(),
            indA.getLiquidez(), indB.getLiquidez(), participacion, true);

        evaluarFinancieroConsorcio(detalles, "Nivel de Endeudamiento", requisito.getEndeudamiento(),
            indA.getEndeudamiento(), indB.getEndeudamiento(), participacion, false);

        evaluarFinancieroConsorcio(detalles, "Capital de Trabajo", requisito.getCapitalTrabajo(),
            indA.getCapitalTrabajo(), indB.getCapitalTrabajo(), participacion, true);

        evaluarFinancieroConsorcio(detalles, "Patrimonio Total", requisito.getPatrimonio(),
            indA.getPatrimonio(), indB.getPatrimonio(), participacion, true);

        Double valorObraSmmlv = requisito.getCuadroDeObra().getValorSMMLV();
        if (valorObraSmmlv != null && valorObraSmmlv > 0) {
            BigDecimal reqExp = BigDecimal.valueOf(valorObraSmmlv);
            BigDecimal expA = ReglaExperiencia.validarExperiencia(a.getExperiencias(), BigDecimal.ZERO, false, 99).totalSmmlv();
            BigDecimal expB = ReglaExperiencia.validarExperiencia(b.getExperiencias(), BigDecimal.ZERO, false, 99).totalSmmlv();
            BigDecimal totalExp = expA.add(expB);
            boolean cumple = totalExp.compareTo(reqExp) >= 0;
            detalles.add(new DetalleRequisito("Experiencia Acumulada (SMMLV)", reqExp, totalExp, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        if (requisito.getKResidualProceso() != null) {
            BigDecimal reqK = BigDecimal.valueOf(requisito.getKResidualProceso());
            BigDecimal totalK = obtenerUltimaK(a).add(obtenerUltimaK(b));
            boolean cumple = totalK.compareTo(reqK) >= 0;
            detalles.add(new DetalleRequisito("Capacidad Residual (K)", reqK, totalK, cumple,
                cumple ? "CUMPLE" : "NO CUMPLE: Suma insuficiente (" + totalK + ")"));
        }

        boolean cumpleGlobal = detalles.stream().allMatch(DetalleRequisito::cumple);
        return new ResultadoEvaluacion(a.getId(), requisito.getCuadroDeObra().getId(), TipoParticipacion.CONSORCIO, cumpleGlobal, detalles);
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

    private void evaluarFinancieroConsorcio(List<DetalleRequisito> detalles, String nombre, Double reqVal,
                                            BigDecimal valA, BigDecimal valB, BigDecimal part, boolean esMayorIgual) {
        if (reqVal == null) return;
        BigDecimal req = BigDecimal.valueOf(reqVal);
        BigDecimal a = valA != null ? valA : BigDecimal.ZERO;
        BigDecimal b = valB != null ? valB : BigDecimal.ZERO;
        BigDecimal actual = a.multiply(part).add(b.multiply(part)).setScale(4, RoundingMode.HALF_UP);
        boolean cumple = esMayorIgual ? actual.compareTo(req) >= 0 : actual.compareTo(req) <= 0;
        detalles.add(new DetalleRequisito(nombre, req, actual, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
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
