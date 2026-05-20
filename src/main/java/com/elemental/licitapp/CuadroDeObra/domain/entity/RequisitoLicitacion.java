package com.elemental.licitapp.CuadroDeObra.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "n_meses")
    private Integer n;

    // --- INDICADORES FINANCIEROS REQUERIDOS ---
    @Column(name = "presupuesto_licitacion")
    private Double presupuesto; // Presupuesto oficial capturado en el modal

    @Column(name = "patrimonio_pliego")
    private Double patrimonio; // El "Patrimonio" que pide el pliego

    @Column(name = "capital_trabajo_req")
    private Double capitalTrabajo; // Capital de Trabajo mínimo requerido

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

    /**
     * Aplica un parche parcial: copia solo los campos no-null del origen.
     * Soporta el caso "actualizar uno o varios campos" desde el frontend
     * sin obligarlo a reenviar el recurso completo.
     */
    public void aplicarPatch(RequisitoLicitacion parche) {
        if (parche == null) return;
        if (parche.general != null) this.general = parche.general;
        if (parche.especifica1 != null) this.especifica1 = parche.especifica1;
        if (parche.especifica2 != null) this.especifica2 = parche.especifica2;
        if (parche.secundaria != null) this.secundaria = parche.secundaria;
        if (parche.contrato != null) this.contrato = parche.contrato;
        if (parche.n != null) this.n = parche.n;
        if (parche.presupuesto != null) this.presupuesto = parche.presupuesto;
        if (parche.patrimonio != null) this.patrimonio = parche.patrimonio;
        if (parche.capitalTrabajo != null) this.capitalTrabajo = parche.capitalTrabajo;
        if (parche.liquidez != null) this.liquidez = parche.liquidez;
        if (parche.endeudamiento != null) this.endeudamiento = parche.endeudamiento;
        if (parche.razonCoberturaInteres != null) this.razonCoberturaInteres = parche.razonCoberturaInteres;
        if (parche.rentabilidadPatrimonio != null) this.rentabilidadPatrimonio = parche.rentabilidadPatrimonio;
        if (parche.rentabilidadActivo != null) this.rentabilidadActivo = parche.rentabilidadActivo;
        if (parche.kResidualProceso != null) this.kResidualProceso = parche.kResidualProceso;
        if (parche.poeAnticipo != null) this.poeAnticipo = parche.poeAnticipo;
    }
}
