package com.elemental.licitapp.Empresa.domain.entity;

import com.elemental.licitapp.Empresa.domain.enums.EstadoIndicador;
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
    // Cada ratio guarda su valor (null si es indeterminado) y un estado que explica el porqué.
    @Column(precision = 20, scale = 4)
    private BigDecimal liquidez;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_liquidez", length = 30)
    private EstadoIndicador estadoLiquidez;

    @Column(precision = 20, scale = 4)
    private BigDecimal endeudamiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_endeudamiento", length = 30)
    private EstadoIndicador estadoEndeudamiento;

    @Column(name = "cobertura_interes", precision = 20, scale = 4)
    private BigDecimal razonCoberturaInteres;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_cobertura_interes", length = 30)
    private EstadoIndicador estadoRazonCoberturaInteres;

    @Column(precision = 20, scale = 2)
    private BigDecimal patrimonio;

    @Column(name = "capital_trabajo", precision = 20, scale = 2)
    private BigDecimal capitalTrabajo;

    // Indicadores de Capacidad Organizacional (Colombia - Decreto 1082/2015)
    @Column(name = "rentabilidad_patrimonio", precision = 20, scale = 4)
    private BigDecimal rentabilidadPatrimonio;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_rentabilidad_patrimonio", length = 30)
    private EstadoIndicador estadoRentabilidadPatrimonio;

    @Column(name = "rentabilidad_activo", precision = 20, scale = 4)
    private BigDecimal rentabilidadActivo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_rentabilidad_activo", length = 30)
    private EstadoIndicador estadoRentabilidadActivo;

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

        // Liquidez (AC/PC): sin pasivo corriente = no debe a corto plazo → favorable.
        ResultadoIndicador liq = ResultadoIndicador.calcular(
                activoCorriente, pasivoCorriente, ESCALA_RATIO, EstadoIndicador.INDETERMINADO_FAVORABLE);
        this.liquidez = liq.valor();
        this.estadoLiquidez = liq.estado();

        // Endeudamiento (PT/AT): sin activos = dato no evaluable → a verificar.
        ResultadoIndicador end = ResultadoIndicador.calcular(
                pasivoTotal, activoTotal, ESCALA_RATIO, EstadoIndicador.INDETERMINADO_A_VERIFICAR);
        this.endeudamiento = end.valor();
        this.estadoEndeudamiento = end.estado();

        // RCI (UtilOp/GastosInt): sin gastos de interés = los cubre por definición → favorable.
        ResultadoIndicador rci = ResultadoIndicador.calcular(
                utilidadOperacional, gastosInteres, ESCALA_RATIO, EstadoIndicador.INDETERMINADO_FAVORABLE);
        this.razonCoberturaInteres = rci.valor();
        this.estadoRazonCoberturaInteres = rci.estado();

        // ROE (UtilOp/Patrimonio): patrimonio 0/negativo = inviabilidad → a verificar.
        ResultadoIndicador roe = ResultadoIndicador.calcular(
                utilidadOperacional, this.patrimonio, ESCALA_RATIO, EstadoIndicador.INDETERMINADO_A_VERIFICAR);
        this.rentabilidadPatrimonio = roe.valor();
        this.estadoRentabilidadPatrimonio = roe.estado();

        // ROA (UtilOp/AT): sin activos = dato no evaluable → a verificar.
        ResultadoIndicador roa = ResultadoIndicador.calcular(
                utilidadOperacional, activoTotal, ESCALA_RATIO, EstadoIndicador.INDETERMINADO_A_VERIFICAR);
        this.rentabilidadActivo = roa.valor();
        this.estadoRentabilidadActivo = roa.estado();
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
}
