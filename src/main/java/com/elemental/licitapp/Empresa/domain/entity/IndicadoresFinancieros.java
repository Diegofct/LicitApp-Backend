package com.elemental.licitapp.Empresa.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@NoArgsConstructor
@Table(name = "indicadores_financieros")
public class IndicadoresFinancieros {

    private static final int ESCALA_RATIO = 4;
    private static final int ESCALA_MONEDA = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "anio_cierre")
    private Integer anioCierre;

    // Valores Absolutos (Necesarios para cálculos de Consorcios)
    @Column(name = "activo_corriente", precision = 20, scale = 2)
    private BigDecimal activoCorriente;

    @Column(name = "pasivo_corriente", precision = 20, scale = 2)
    private BigDecimal pasivoCorriente;

    @Column(name = "activo_total", precision = 20, scale = 2)
    private BigDecimal activoTotal;

    @Column(name = "pasivo_total", precision = 20, scale = 2)
    private BigDecimal pasivoTotal;

    @Column(name = "utilidad_operacional", precision = 20, scale = 2)
    private BigDecimal utilidadOperacional;

    @Column(name = "gastos_interes", precision = 20, scale = 2)
    private BigDecimal gastosInteres;

    // Indicadores de Capacidad Financiera (Colombia - Decreto 1082/2015)
    @Column(precision = 20, scale = 4)
    private BigDecimal liquidez;

    @Column(precision = 20, scale = 4)
    private BigDecimal endeudamiento;

    @Column(name = "cobertura_interes", precision = 20, scale = 4)
    private BigDecimal razonCoberturaInteres;

    @Column(precision = 20, scale = 2)
    private BigDecimal patrimonio;

    @Column(name = "capital_trabajo", precision = 20, scale = 2)
    private BigDecimal capitalTrabajo;

    // Indicadores de Capacidad Organizacional (Colombia - Decreto 1082/2015)
    @Column(name = "rentabilidad_patrimonio", precision = 20, scale = 4)
    private BigDecimal rentabilidadPatrimonio;

    @Column(name = "rentabilidad_activo", precision = 20, scale = 4)
    private BigDecimal rentabilidadActivo;

    @OneToOne
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;

    /**
     * Recalcula todos los indicadores derivados a partir de los valores absolutos.
     * El patrimonio se calcula primero porque ROE depende de él.
     */
    public void recalcular() {
        this.patrimonio = calcularPatrimonio(activoTotal, pasivoTotal);
        this.capitalTrabajo = calcularCapitalTrabajo(activoCorriente, pasivoCorriente);
        this.liquidez = ratio(activoCorriente, pasivoCorriente);
        this.endeudamiento = ratio(pasivoTotal, activoTotal);
        this.razonCoberturaInteres = ratio(utilidadOperacional, gastosInteres);
        this.rentabilidadPatrimonio = ratio(utilidadOperacional, this.patrimonio);
        this.rentabilidadActivo = ratio(utilidadOperacional, activoTotal);
    }

    public static BigDecimal calcularLiquidez(BigDecimal activoCorriente, BigDecimal pasivoCorriente) {
        return ratio(activoCorriente, pasivoCorriente);
    }

    public static BigDecimal calcularEndeudamiento(BigDecimal pasivoTotal, BigDecimal activoTotal) {
        return ratio(pasivoTotal, activoTotal);
    }

    public static BigDecimal calcularRCI(BigDecimal utilidadOperacional, BigDecimal gastosInteres) {
        return ratio(utilidadOperacional, gastosInteres);
    }

    public static BigDecimal calcularROE(BigDecimal utilidadOperacional, BigDecimal patrimonio) {
        return ratio(utilidadOperacional, patrimonio);
    }

    public static BigDecimal calcularROA(BigDecimal utilidadOperacional, BigDecimal activoTotal) {
        return ratio(utilidadOperacional, activoTotal);
    }

    public static BigDecimal calcularCapitalTrabajo(BigDecimal activoCorriente, BigDecimal pasivoCorriente) {
        BigDecimal ac = activoCorriente != null ? activoCorriente : BigDecimal.ZERO;
        BigDecimal pc = pasivoCorriente != null ? pasivoCorriente : BigDecimal.ZERO;
        return ac.subtract(pc).setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    public static BigDecimal calcularPatrimonio(BigDecimal activoTotal, BigDecimal pasivoTotal) {
        if (activoTotal == null || pasivoTotal == null) return null;
        return activoTotal.subtract(pasivoTotal).setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    private static BigDecimal ratio(BigDecimal numerador, BigDecimal denominador) {
        if (numerador == null || denominador == null || denominador.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerador.divide(denominador, ESCALA_RATIO, RoundingMode.HALF_UP);
    }
}
