package com.elemental.licitapp.Empresa.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "capacidad_residual_proponente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacidadResidualProponente {

    private static final BigDecimal CIEN = new BigDecimal("100");
    private static final int ESCALA_MONEDA = 2;
    private static final int ESCALA_CALCULO = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @Column(name = "capacidad_organizacion")
    private BigDecimal capacidadOrganizacion;

    private BigDecimal experiencia;

    @Column(name = "capacidad_tecnica")
    private BigDecimal capacidadTecnica;

    @Column(name = "capacidad_financiera")
    private BigDecimal capacidadFinanciera;

    @Column(name = "saldos_contratos_ejecucion")
    private BigDecimal saldosContratosEjecucion;

    @Column(name = "resultado_k")
    private BigDecimal resultadoK;

    /**
     * Recalcula el resultado K (CRP) usando los puntajes y saldos actuales.
     */
    public void recalcular() {
        this.resultadoK = calcularCRP(
                capacidadOrganizacion,
                experiencia,
                capacidadTecnica,
                capacidadFinanciera,
                saldosContratosEjecucion
        );
    }

    /**
     * CRP = CO * [(E + CT + CF) / 100] - SCE
     */
    public static BigDecimal calcularCRP(BigDecimal co, BigDecimal e, BigDecimal ct, BigDecimal cf, BigDecimal sce) {
        BigDecimal sumaPuntajes = safe(e).add(safe(ct)).add(safe(cf));
        BigDecimal factor = sumaPuntajes.divide(CIEN, ESCALA_CALCULO, RoundingMode.HALF_UP);
        return safe(co).multiply(factor)
                .subtract(safe(sce))
                .setScale(ESCALA_MONEDA, RoundingMode.HALF_UP);
    }

    private static BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
