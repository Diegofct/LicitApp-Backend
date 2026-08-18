package com.elemental.licitapp.Sobre2.domain.entity;

import com.elemental.licitapp.Sobre2.domain.enums.MetodoPonderacion;
import com.elemental.licitapp.Sobre2.domain.enums.RegimenPonderacion;

import java.math.BigDecimal;
import java.util.List;

/**
 * Analisis completo del Sobre 2 de un proceso: los metodos de ponderacion aplicables y el
 * valor con el que conviene presentarse.
 *
 * @param valorSugerido       valor que maximiza el puntaje esperado cuando no se sabe que metodo
 *                            sorteara la TRM (mediana ponderada de las referencias)
 * @param advertencias        cosas que el analista debe saber antes de decidir (muestra pequena,
 *                            sin presupuesto oficial, ofertas en otra moneda, etc.)
 * @param estadoCuadro        estado del cuadro de obra al momento del analisis
 * @param listoParaDecidir    ya hay informe de evaluacion definitivo: es el momento de decidir
 * @param valoresDefinitivos  el Sobre 2 ya se abrio en audiencia: los valores son fiables
 * @param metodoSugerido      metodo del que sale {@code valorSugerido}, es decir, a que
 *                            tendencia se le esta apuntando; null si no hubo referencias
 */
public record AnalisisSobre2(
        Long cuadroDeObraId,
        String numeroProceso,
        BigDecimal presupuestoOficial,
        RegimenPonderacion regimen,
        int totalOferentes,
        int oferentesValidos,
        BigDecimal puntajeMaximo,
        BigDecimal valorCandidato,
        BigDecimal porcentajeCandidato,
        BigDecimal valorSugerido,
        BigDecimal porcentajeSugerido,
        List<ResultadoMetodo> metodos,
        List<String> advertencias,
        // Campos anadidos al final a proposito: el frontend ya consume este contrato.
        String estadoCuadro,
        boolean listoParaDecidir,
        boolean valoresDefinitivos,
        MetodoPonderacion metodoSugerido
) {
}
