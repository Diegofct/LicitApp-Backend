package com.elemental.licitapp.CuadroDeObra.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

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

    @Column(name = "plazo_meses")
    private Integer plazo; // Plazo del proceso en meses (RF5, precargado desde el Cuadro de Obra)

    // --- INDICADORES FINANCIEROS REQUERIDOS ---
    // Montos en COP: scale 2, precision 20 (paridad con IndicadoresFinancieros).
    @Column(name = "presupuesto_licitacion", precision = 20, scale = 2)
    private BigDecimal presupuesto; // Presupuesto oficial capturado en el modal

    @Column(name = "patrimonio_pliego", precision = 20, scale = 2)
    private BigDecimal patrimonio; // El "Patrimonio" que pide el pliego

    @Column(name = "capital_trabajo_req", precision = 20, scale = 2)
    private BigDecimal capitalTrabajo; // Capital de Trabajo mínimo requerido

    // Ratios/indices: scale 4, precision 20 (paridad con IndicadoresFinancieros).
    @Column(name = "liquidez_req", precision = 20, scale = 4)
    private BigDecimal liquidez;

    @Column(name = "endeudamiento_req", precision = 20, scale = 4)
    private BigDecimal endeudamiento;

    @Column(name = "cobertura_interes_req", precision = 20, scale = 4)
    private BigDecimal razonCoberturaInteres;

    @Column(name = "rentabilidad_patrimonio_req", precision = 20, scale = 4)
    private BigDecimal rentabilidadPatrimonio;

    @Column(name = "rentabilidad_activo_req", precision = 20, scale = 4)
    private BigDecimal rentabilidadActivo;

    // --- CAPACIDAD RESIDUAL (K) ---
    @Column(name = "k_residual_proceso", precision = 20, scale = 2)
    private BigDecimal kResidualProceso;

    // poeAnticipo se mantiene como Double por decision explicita (no es monto COP
    // ni se compara contra indicadores; no lo consume ningun calculo).
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
        if (parche.plazo != null) this.plazo = parche.plazo;
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
