package com.elemental.licitapp.AnalisisDeCumplimiento.domain.service.reglas;

import com.elemental.licitapp.Empresa.domain.entity.Experiencia;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReglaExperiencia {

    public record ResultadoExperiencia(
        BigDecimal totalSmmlv,
        int contratosUsados,
        boolean cumple
    ) {}

    /**
     * Valida la experiencia de una empresa frente a un requerimiento de SMMLV.
     *
     * @param experiencias Lista de experiencias de la empresa.
     * @param requeridoSmmlv Valor requerido de experiencia en SMMLV.
     * @param esMipymeOMujer Indica si la empresa es Mipyme o propiedad de mujeres.
     * @param limiteContratosPliego Límite de contratos establecidos en el pliego (generalmente 5).
     * @return ResultadoExperiencia indicando el total acumulado y si cumple con el requisito.
     */
    public static ResultadoExperiencia validarExperiencia(
        List<Experiencia> experiencias,
        BigDecimal requeridoSmmlv,
        boolean esMipymeOMujer,
        int limiteContratosPliego
    ) {
        if (experiencias == null || experiencias.isEmpty()) {
            return new ResultadoExperiencia(BigDecimal.ZERO, 0, false);
        }

        // Determinar el límite real de contratos (según el pliego o el beneficio de ley)
        // La ley colombiana suele dar hasta 2 contratos adicionales (5 -> 7) si cumple Mipyme/Mujer.
        // Asumimos que si esMipymeOMujer es true, se le suman 2 al límite del pliego o se fija en 7 si el pliego era 5.
        // Si el pliego no define límite (0 o menor), asumimos 5 como base legal estándar.
        int limiteBase = limiteContratosPliego > 0 ? limiteContratosPliego : 5;
        int limiteFinal = esMipymeOMujer ? limiteBase + 2 : limiteBase;

        // Filtrar experiencias válidas y ordenarlas de mayor a menor valor en SMMLV
        List<Experiencia> experienciasOrdenadas = experiencias.stream()
            .filter(e -> e.getValorSMMLV() != null && e.getValorSMMLV() > 0)
            .sorted(Comparator.comparing(Experiencia::getValorSMMLV).reversed())
            .toList();

        // Tomar hasta el límite permitido
        List<Experiencia> mejoresContratos = experienciasOrdenadas.stream()
            .limit(limiteFinal)
            .toList();

        // Sumar el valor en SMMLV de los contratos seleccionados
        BigDecimal sumaSmmlv = mejoresContratos.stream()
            .map(e -> BigDecimal.valueOf(e.getValorSMMLV()))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Verificar si cumple
        boolean cumple = requeridoSmmlv == null || sumaSmmlv.compareTo(requeridoSmmlv) >= 0;

        return new ResultadoExperiencia(sumaSmmlv, mejoresContratos.size(), cumple);
    }
}
