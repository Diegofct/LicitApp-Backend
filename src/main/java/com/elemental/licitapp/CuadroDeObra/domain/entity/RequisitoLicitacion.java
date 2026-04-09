package com.elemental.licitapp.CuadroDeObra.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "requisitos_licitacion")
public class RequisitoLicitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cuadro_de_obra_id", nullable = false)
    private CuadroDeObra cuadroDeObra;

    // --- DATOS Y EXPERIENCIA ---
    @Column(name = "exp_general", columnDefinition = "TEXT")
    private String general;

    @Column(name = "exp_especifica_1", columnDefinition = "TEXT")
    private String especifica1;

    @Column(name = "exp_especifica_2", columnDefinition = "TEXT")
    private String especifica2;

    @Column(name = "exp_secundaria", columnDefinition = "TEXT")
    private String secundaria;

    @Column(name = "num_contrato")
    private Integer contrato; // Cantidad de contratos permitidos

    // --- INDICADORES FINANCIEROS REQUERIDOS ---
    @Column(name = "ct_proceso")
    private Double ctProceso;  // Capital de Trabajo del Proceso

    @Column(name = "n_meses")
    private int n;

    @Column(name = "patrimonio_pliego")
    private Double patrimonio; // El "Patrimonio" que pide el pliego

    // Nuevos campos para comparación completa
    @Column(name = "liquidez_req")
    private Double liquidez;

    @Column(name = "endeudamiento_req")
    private Double endeudamiento;

    @Column(name = "cobertura_interes_req")
    private Double razonCoberturaInteres;

    @Column(name = "rentabilidad_patrimonio_req")
    private Double rentabilidadPatrimonio;

    @Column(name = "rentabilidad_activo_req")
    private Double rentabilidadActivo;

    // --- CAPACIDAD RESIDUAL (K) ---
    @Column(name = "k_residual_proceso")
    private Double kResidualProceso;

    @Column(name = "poe_anticipo")
    private Double poeAnticipo; // El % de anticipo según el POE
}
