package com.elemental.licitapp.AnalisisDeCumplimiento.domain.service;

import com.elemental.licitapp.AnalisisDeCumplimiento.domain.model.*;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaCapacidadResidual;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaExperiencia;
import com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas.ReglaFinanciera;
import com.elemental.licitapp.CuadroDeObra.domain.entity.RequisitoLicitacion;
import com.elemental.licitapp.Empresa.domain.entity.Empresa;
import com.elemental.licitapp.Empresa.domain.entity.IndicadoresFinancieros;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnalizadorCumplimientoService {

    /**
     * Orquesta la evaluación de los requisitos financieros y de experiencia de una empresa frente a una licitación.
     */
    public ResultadoEvaluacion analizar(Empresa empresa, RequisitoLicitacion requisito, TipoParticipacion tipo) {
        List<DetalleRequisito> detalles = new ArrayList<>();
        IndicadoresFinancieros ind = empresa.getIndicadores();

        if (ind == null) {
            return new ResultadoEvaluacion(empresa.getId(), requisito.getCuadroDeObra().getId(), tipo, false, List.of(
                new DetalleRequisito("General", BigDecimal.ZERO, BigDecimal.ZERO, false, "La empresa no tiene indicadores financieros registrados.")
            ));
        }

        // 1. Evaluación de Liquidez (Activo Corriente / Pasivo Corriente)
        if (requisito.getLiquidez() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getLiquidez());
            BigDecimal actual = ReglaFinanciera.calcularLiquidez(ind.getActivoCorriente(), ind.getPasivoCorriente());
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("Índice de Liquidez", req, actual, cumple, 
                cumple ? "CUMPLE" : "NO CUMPLE: El indicador es menor al requerido (" + req + ")"));
        }

        // 2. Evaluación de Endeudamiento (Pasivo Total / Activo Total) -> Debe ser menor o igual al requisito
        if (requisito.getEndeudamiento() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getEndeudamiento());
            BigDecimal actual = ReglaFinanciera.calcularEndeudamiento(ind.getPasivoTotal(), ind.getActivoTotal());
            boolean cumple = actual.compareTo(req) <= 0;
            detalles.add(new DetalleRequisito("Nivel de Endeudamiento", req, actual, cumple, 
                cumple ? "CUMPLE" : "NO CUMPLE: El indicador supera el máximo permitido (" + req + ")"));
        }

        // 3. Capital de Trabajo (Activo Corriente - Pasivo Corriente)
        if (requisito.getCapitalTrabajo() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getCapitalTrabajo());
            BigDecimal actual = ReglaFinanciera.calcularCapitalTrabajo(ind.getActivoCorriente(), ind.getPasivoCorriente());
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("Capital de Trabajo", req, actual, cumple, 
                cumple ? "CUMPLE" : "NO CUMPLE: Déficit de " + req.subtract(actual)));
        }

        // 4. Patrimonio (Valor Absoluto)
        if (requisito.getPatrimonio() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getPatrimonio());
            BigDecimal actual = ind.getPatrimonio() != null ? ind.getPatrimonio() : BigDecimal.ZERO;
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("Patrimonio Total", req, actual, cumple, 
                cumple ? "CUMPLE" : "NO CUMPLE: Déficit de " + req.subtract(actual)));
        }

        // 5. Razón de Cobertura de Intereses (Utilidad Operacional / Gastos Intereses)
        if (requisito.getRazonCoberturaInteres() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getRazonCoberturaInteres());
            BigDecimal actual = ReglaFinanciera.calcularRCI(ind.getUtilidadOperacional(), ind.getGastosInteres());
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("Cobertura de Intereses", req, actual, cumple, 
                cumple ? "CUMPLE" : "NO CUMPLE: El indicador es menor al requerido (" + req + ")"));
        }

        // 6. Rentabilidad sobre Patrimonio (ROE)
        if (requisito.getRentabilidadPatrimonio() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getRentabilidadPatrimonio());
            BigDecimal actual = ReglaFinanciera.calcularROE(ind.getUtilidadOperacional(), ind.getPatrimonio());
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("ROE (Rentabilidad sobre Patrimonio)", req, actual, cumple, 
                cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 7. Rentabilidad del Activo (ROA)
        if (requisito.getRentabilidadActivo() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getRentabilidadActivo());
            BigDecimal actual = ReglaFinanciera.calcularROA(ind.getUtilidadOperacional(), ind.getActivoTotal());
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("ROA (Rentabilidad sobre Activos)", req, actual, cumple, 
                cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 8. Validación de Experiencia
        Double valorObraSmmlv = requisito.getCuadroDeObra().getValorSMMLV();
        if (valorObraSmmlv != null && valorObraSmmlv > 0) {
            BigDecimal reqExp = BigDecimal.valueOf(valorObraSmmlv);
            
            // Determinar si la empresa tiene beneficio Mipyme/Mujer basándonos en tamaño o alguna política
            String tamano = empresa.getTamanoEmpresa() != null ? empresa.getTamanoEmpresa().toLowerCase() : "";
            boolean esMipymeOMujer = tamano.contains("micro") || tamano.contains("pequeña") || tamano.contains("mediana") || tamano.contains("mipyme") || tamano.contains("mujer");
            
            Integer limiteContratos = requisito.getContrato() != null ? requisito.getContrato() : 5;

            ReglaExperiencia.ResultadoExperiencia resExp = ReglaExperiencia.validarExperiencia(
                empresa.getExperiencias(), 
                reqExp, 
                esMipymeOMujer, 
                limiteContratos
            );

            detalles.add(new DetalleRequisito(
                "Experiencia Acumulada (SMMLV)", 
                reqExp, 
                resExp.totalSmmlv(), 
                resExp.cumple(), 
                resExp.cumple() ? "CUMPLE: Acumulado con " + resExp.contratosUsados() + " contratos." 
                                : "NO CUMPLE: Acumulado " + resExp.totalSmmlv() + " con " + resExp.contratosUsados() + " contratos."
            ));
        }

        // 9. Validación de Capacidad Residual (K)
        if (requisito.getKResidualProceso() != null) {
            BigDecimal reqK = BigDecimal.valueOf(requisito.getKResidualProceso());
            BigDecimal crp = ReglaCapacidadResidual.calcularCRP(
                empresa.getCapacidadOrganizacion(),
                empresa.getPuntajeExperiencia(),
                empresa.getPuntajeTecnico(),
                empresa.getPuntajeFinanciero(),
                empresa.getSaldosContratosEjecucion()
            );
            boolean cumple = crp.compareTo(reqK) >= 0;
            detalles.add(new DetalleRequisito(
                "Capacidad Residual (K)",
                reqK,
                crp,
                cumple,
                cumple ? "CUMPLE" : "NO CUMPLE: Déficit de " + reqK.subtract(crp)
            ));
        }

        boolean cumpleGlobal = detalles.stream().allMatch(DetalleRequisito::cumple);

        return new ResultadoEvaluacion(empresa.getId(), requisito.getCuadroDeObra().getId(), tipo, cumpleGlobal, detalles);
    }

    /**
     * Evalúa dos empresas bajo una figura de Consorcio (50% - 50%).
     */
    public ResultadoEvaluacion evaluarConsorcio(Empresa a, Empresa b, RequisitoLicitacion requisito) {
        List<DetalleRequisito> detalles = new ArrayList<>();
        IndicadoresFinancieros indA = a.getIndicadores();
        IndicadoresFinancieros indB = b.getIndicadores();

        if (indA == null || indB == null) {
            return new ResultadoEvaluacion(a.getId(), requisito.getCuadroDeObra().getId(), TipoParticipacion.CONSORCIO, false, List.of(
                new DetalleRequisito("General", BigDecimal.ZERO, BigDecimal.ZERO, false, "Una de las empresas no tiene indicadores financieros registrados.")
            ));
        }

        BigDecimal participacion = new BigDecimal("0.5");

        // 1. Evaluación de Liquidez
        if (requisito.getLiquidez() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getLiquidez());
            BigDecimal liqA = ReglaFinanciera.calcularLiquidez(indA.getActivoCorriente(), indA.getPasivoCorriente());
            BigDecimal liqB = ReglaFinanciera.calcularLiquidez(indB.getActivoCorriente(), indB.getPasivoCorriente());
            BigDecimal actual = liqA.multiply(participacion).add(liqB.multiply(participacion));
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("Índice de Liquidez", req, actual, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 2. Evaluación de Endeudamiento
        if (requisito.getEndeudamiento() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getEndeudamiento());
            BigDecimal endA = ReglaFinanciera.calcularEndeudamiento(indA.getPasivoTotal(), indA.getActivoTotal());
            BigDecimal endB = ReglaFinanciera.calcularEndeudamiento(indB.getPasivoTotal(), indB.getActivoTotal());
            BigDecimal actual = endA.multiply(participacion).add(endB.multiply(participacion));
            boolean cumple = actual.compareTo(req) <= 0;
            detalles.add(new DetalleRequisito("Nivel de Endeudamiento", req, actual, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 3. Capital de Trabajo
        if (requisito.getCapitalTrabajo() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getCapitalTrabajo());
            BigDecimal ctA = ReglaFinanciera.calcularCapitalTrabajo(indA.getActivoCorriente(), indA.getPasivoCorriente());
            BigDecimal ctB = ReglaFinanciera.calcularCapitalTrabajo(indB.getActivoCorriente(), indB.getPasivoCorriente());
            BigDecimal actual = ctA.multiply(participacion).add(ctB.multiply(participacion));
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("Capital de Trabajo", req, actual, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 4. Patrimonio
        if (requisito.getPatrimonio() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getPatrimonio());
            BigDecimal patA = indA.getPatrimonio() != null ? indA.getPatrimonio() : BigDecimal.ZERO;
            BigDecimal patB = indB.getPatrimonio() != null ? indB.getPatrimonio() : BigDecimal.ZERO;
            BigDecimal actual = patA.multiply(participacion).add(patB.multiply(participacion));
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("Patrimonio Total", req, actual, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 5. Razón de Cobertura de Intereses
        if (requisito.getRazonCoberturaInteres() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getRazonCoberturaInteres());
            BigDecimal rciA = ReglaFinanciera.calcularRCI(indA.getUtilidadOperacional(), indA.getGastosInteres());
            BigDecimal rciB = ReglaFinanciera.calcularRCI(indB.getUtilidadOperacional(), indB.getGastosInteres());
            BigDecimal actual = rciA.multiply(participacion).add(rciB.multiply(participacion));
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("Cobertura de Intereses", req, actual, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 6. ROE
        if (requisito.getRentabilidadPatrimonio() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getRentabilidadPatrimonio());
            BigDecimal roeA = ReglaFinanciera.calcularROE(indA.getUtilidadOperacional(), indA.getPatrimonio());
            BigDecimal roeB = ReglaFinanciera.calcularROE(indB.getUtilidadOperacional(), indB.getPatrimonio());
            BigDecimal actual = roeA.multiply(participacion).add(roeB.multiply(participacion));
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("ROE (Rentabilidad sobre Patrimonio)", req, actual, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 7. ROA
        if (requisito.getRentabilidadActivo() != null) {
            BigDecimal req = BigDecimal.valueOf(requisito.getRentabilidadActivo());
            BigDecimal roaA = ReglaFinanciera.calcularROA(indA.getUtilidadOperacional(), indA.getActivoTotal());
            BigDecimal roaB = ReglaFinanciera.calcularROA(indB.getUtilidadOperacional(), indB.getActivoTotal());
            BigDecimal actual = roaA.multiply(participacion).add(roaB.multiply(participacion));
            boolean cumple = actual.compareTo(req) >= 0;
            detalles.add(new DetalleRequisito("ROA (Rentabilidad sobre Activos)", req, actual, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 8. Validación de Experiencia
        Double valorObraSmmlv = requisito.getCuadroDeObra().getValorSMMLV();
        if (valorObraSmmlv != null && valorObraSmmlv > 0) {
            BigDecimal reqExp = BigDecimal.valueOf(valorObraSmmlv);
            Integer limiteContratos = requisito.getContrato() != null ? requisito.getContrato() : 5;

            // Experiencia de A
            String tamanoA = a.getTamanoEmpresa() != null ? a.getTamanoEmpresa().toLowerCase() : "";
            boolean esMipymeA = tamanoA.contains("micro") || tamanoA.contains("pequeña") || tamanoA.contains("mediana") || tamanoA.contains("mipyme") || tamanoA.contains("mujer");
            ReglaExperiencia.ResultadoExperiencia expA = ReglaExperiencia.validarExperiencia(a.getExperiencias(), BigDecimal.ZERO, esMipymeA, limiteContratos);

            // Experiencia de B
            String tamanoB = b.getTamanoEmpresa() != null ? b.getTamanoEmpresa().toLowerCase() : "";
            boolean esMipymeB = tamanoB.contains("micro") || tamanoB.contains("pequeña") || tamanoB.contains("mediana") || tamanoB.contains("mipyme") || tamanoB.contains("mujer");
            ReglaExperiencia.ResultadoExperiencia expB = ReglaExperiencia.validarExperiencia(b.getExperiencias(), BigDecimal.ZERO, esMipymeB, limiteContratos);

            BigDecimal totalExp = expA.totalSmmlv().add(expB.totalSmmlv());
            boolean cumple = totalExp.compareTo(reqExp) >= 0;

            detalles.add(new DetalleRequisito("Experiencia Acumulada (SMMLV)", reqExp, totalExp, cumple, cumple ? "CUMPLE" : "NO CUMPLE"));
        }

        // 9. Validación de Capacidad Residual (K)
        if (requisito.getKResidualProceso() != null) {
            BigDecimal reqK = BigDecimal.valueOf(requisito.getKResidualProceso());
            BigDecimal crpA = ReglaCapacidadResidual.calcularCRP(
                a.getCapacidadOrganizacion(),
                a.getPuntajeExperiencia(),
                a.getPuntajeTecnico(),
                a.getPuntajeFinanciero(),
                a.getSaldosContratosEjecucion()
            );
            BigDecimal crpB = ReglaCapacidadResidual.calcularCRP(
                b.getCapacidadOrganizacion(),
                b.getPuntajeExperiencia(),
                b.getPuntajeTecnico(),
                b.getPuntajeFinanciero(),
                b.getSaldosContratosEjecucion()
            );
            BigDecimal totalCRP = crpA.add(crpB);
            boolean cumple = totalCRP.compareTo(reqK) >= 0;
            detalles.add(new DetalleRequisito(
                "Capacidad Residual (K)",
                reqK,
                totalCRP,
                cumple,
                cumple ? "CUMPLE" : "NO CUMPLE"
            ));
        }

        boolean cumpleGlobal = detalles.stream().allMatch(DetalleRequisito::cumple);

        return new ResultadoEvaluacion(a.getId(), requisito.getCuadroDeObra().getId(), TipoParticipacion.CONSORCIO, cumpleGlobal, detalles);
    }
}
