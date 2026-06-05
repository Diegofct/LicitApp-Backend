package com.elemental.licitapp.AnalisisDeCumplimiento.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reparte el porcentaje complementario (1 − %solicitante) entre las empresas candidatas
 * de un consorcio sugerido, de forma <b>proporcional al aporte</b> de cada una.
 *
 * <p>El "aporte" es un peso adimensional que calcula el llamador (típicamente la fuerza de
 * la candidata en los rubros deficitarios). Si todos los pesos son nulos/cero, el reparto
 * es equitativo. El resultado garantiza que la suma de las participaciones de las candidatas
 * sea exactamente igual al complemento: el residuo de redondeo se imputa al último integrante.</p>
 *
 * <p>Dominio puro: sin Spring, sin JPA, sin estado.</p>
 */
public final class RepartidorParticipacion {

    private static final int ESCALA = 4;

    private RepartidorParticipacion() {}

    /**
     * @param complemento fracción total a repartir, en (0,1) (ej. 0.4 si el solicitante toma 0.6).
     * @param pesos       peso de aporte por empresaId, preservando el orden de inserción.
     * @return participación por empresaId (suma == complemento, escala {@value #ESCALA}).
     */
    public static Map<Long, BigDecimal> repartir(BigDecimal complemento, Map<Long, BigDecimal> pesos) {
        if (pesos == null || pesos.isEmpty()) {
            return Map.of();
        }
        BigDecimal sumaPesos = pesos.values().stream()
                .map(p -> p != null ? p : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        boolean usarEquitativo = sumaPesos.compareTo(BigDecimal.ZERO) <= 0;
        BigDecimal cantidad = BigDecimal.valueOf(pesos.size());

        Map<Long, BigDecimal> resultado = new LinkedHashMap<>();
        BigDecimal acumulado = BigDecimal.ZERO;
        int i = 0;
        for (Map.Entry<Long, BigDecimal> e : pesos.entrySet()) {
            i++;
            BigDecimal participacion;
            if (i == pesos.size()) {
                // El último absorbe el residuo para que la suma sea exacta.
                participacion = complemento.subtract(acumulado).setScale(ESCALA, RoundingMode.HALF_UP);
            } else if (usarEquitativo) {
                participacion = complemento.divide(cantidad, ESCALA, RoundingMode.HALF_UP);
            } else {
                BigDecimal peso = e.getValue() != null ? e.getValue() : BigDecimal.ZERO;
                participacion = complemento.multiply(peso)
                        .divide(sumaPesos, ESCALA, RoundingMode.HALF_UP);
            }
            resultado.put(e.getKey(), participacion);
            acumulado = acumulado.add(participacion);
        }
        return resultado;
    }
}
