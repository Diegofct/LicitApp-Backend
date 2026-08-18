package com.elemental.licitapp.Sobre2.domain.entity;

import com.elemental.licitapp.Sobre2.domain.enums.OrigenOferente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Un proponente que presento oferta economica (Sobre 2) a un proceso.
 *
 * <p>Se persiste tanto del proceso en curso como de procesos ya cerrados: acumular el
 * historico es lo que permite saber a que porcentaje suele presentarse cada competidor.
 */
@Entity
@Table(name = "oferente_proceso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class OferenteProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuadro_de_obra_id", nullable = false)
    private Long cuadroDeObraId;

    /**
     * Identificador de la oferta en SECOP II ({@code CO1.RPL.xxxxxxx}). Es la clave de
     * deduplicacion y de idempotencia de la reimportacion. Queda NULL en las altas manuales:
     * MySQL admite varios NULL en un indice UNIQUE, asi que no colisionan entre si.
     */
    @Column(name = "identificador_oferta", length = 64)
    private String identificadorOferta;

    @Column(name = "referencia_oferta", length = 500)
    private String referenciaOferta;

    @Column(name = "nombre_oferente", nullable = false, length = 500)
    private String nombreOferente;

    /** NULL para consorcios y uniones temporales: SECOP no publica ahi un NIT utilizable. */
    @Column(name = "nit_oferente", length = 32)
    private String nitOferente;

    @Column(name = "valor_oferta", nullable = false, precision = 20, scale = 2)
    private BigDecimal valorOferta;

    /**
     * (valor_oferta / presupuesto_oficial) * 100, truncado a 2 decimales. Snapshot del
     * momento de la importacion: SECOP no publica el porcentaje, se deriva.
     */
    @Column(name = "porcentaje", precision = 9, scale = 2)
    private BigDecimal porcentaje;

    @Column(name = "moneda", length = 16)
    private String moneda;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;

    /**
     * Falso cuando la entidad rechaza la oferta (p. ej. precio artificialmente bajo) o
     * cuando viene en una moneda distinta a COP. Las ofertas no validas no entran a las
     * formulas pero se conservan como rastro del proceso.
     */
    @Column(name = "valida", nullable = false)
    private Boolean valida;

    @Enumerated(EnumType.STRING)
    @Column(name = "origen", nullable = false, length = 16)
    private OrigenOferente origen;

    @Column(name = "fecha_importacion")
    private LocalDateTime fechaImportacion;

    // --- AUDITORIA DE AUTORIA ---
    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @CreatedBy
    @Column(name = "creado_por", updatable = false)
    private Long creadoPor;

    @LastModifiedBy
    @Column(name = "actualizado_por")
    private Long actualizadoPor;

    /** true si la oferta entra a las formulas de ponderacion. */
    /**
     * Si esta oferta entra a la muestra sobre la que se calculan las medidas de tendencia.
     *
     * <p>Se excluyen tres casos: las marcadas como no validas por el analista (rechazadas por
     * la entidad, o en moneda distinta a COP), las de valor no positivo, y las que
     * <b>superan el presupuesto oficial</b>. Esta ultima es causal de rechazo, asi que una
     * oferta asi no compite: dejarla dentro inflaria la mediana y las medias hacia arriba y
     * llevaria a ofertar por encima de lo debido.
     *
     * <p>La oferta se conserva en base de datos y el flag {@code valida} no se toca: excluir
     * del calculo no es lo mismo que pisar la decision del analista.
     *
     * @param presupuestoOficial si es null o no positivo la regla del tope no se puede
     *                           aplicar y solo se evaluan las otras dos
     */
    public boolean participaEnLasFormulas(BigDecimal presupuestoOficial) {
        if (!Boolean.TRUE.equals(valida) || valorOferta == null || valorOferta.signum() <= 0) {
            return false;
        }
        return !superaElPresupuesto(presupuestoOficial);
    }

    public boolean superaElPresupuesto(BigDecimal presupuestoOficial) {
        return presupuestoOficial != null
                && presupuestoOficial.signum() > 0
                && valorOferta != null
                && valorOferta.compareTo(presupuestoOficial) > 0;
    }
}
