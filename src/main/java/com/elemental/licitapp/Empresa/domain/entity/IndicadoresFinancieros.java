package com.elemental.licitapp.Empresa.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Data
@NoArgsConstructor
@Table(name = "indicadores_financieros")
public class IndicadoresFinancieros {

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
    private Double liquidez;
    private Double endeudamiento;

    @Column(name = "cobertura_interes")
    private Double razonCoberturaInteres;

    @Column(precision = 20, scale = 2)
    private BigDecimal patrimonio;

    @Column(name = "capital_trabajo", precision = 20, scale = 2)
    private BigDecimal capitalTrabajo;

    // Indicadores de Capacidad Organizacional (Colombia - Decreto 1082/2015)
    @Column(name = "rentabilidad_patrimonio")
    private Double rentabilidadPatrimonio; // Utilidad Operacional / Patrimonio

    @Column(name = "rentabilidad_activo")
    private Double rentabilidadActivo; // Utilidad Operacional / Activo Total

    @OneToOne
    @JoinColumn(name = "empresa_id")
    @JsonIgnore
    private Empresa empresa;

}
