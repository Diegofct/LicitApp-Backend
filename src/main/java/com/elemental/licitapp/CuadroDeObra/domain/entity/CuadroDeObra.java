package com.elemental.licitapp.CuadroDeObra.domain.entity;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import com.elemental.licitapp.CuadroDeObra.domain.enums.PresentacionMarca;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "cuadro_de_obra")
@EntityListeners(AuditingEntityListener.class)
public class CuadroDeObra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entidad_contratante", length = 500)
    private String entidadContratante;

    @Column(name = "num_proceso")
    private String numeroProceso;

    /**
     * Identificador unico del proceso en SECOP II. Es la identidad del cuadro frente a
     * una licitacion: {@code numeroProceso} no sirve porque solo es unico dentro de una
     * misma entidad. Queda NULL en los cuadros cargados a mano, que no existen en SECOP.
     */
    @Column(name = "id_del_proceso")
    private String idDelProceso;

    @Lob
    @Column(name = "descripcion_objeto", columnDefinition = "TEXT")
    private String descripcionObjeto;

    @Column(name = "estado_proceso")
    private String estadoProceso;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @Column(name = "monto")
    private BigDecimal monto;

    @Column(name = "valor_smmlv")
    private Double valorSMMLV;

    @Column(name = "tipo_proyecto")
    private String tipoProyecto;

    @Column(name = "departamento")
    private String departamento;

    @Column(name = "municipio")
    private String municipio;

    @Lob
    @Column(name = "experiencia", columnDefinition = "TEXT")
    private String experiencia;

    @Column(name = "plazo")
    private Integer plazo;

    @Column(name = "anticipo")
    private Integer anticipo;

    @Lob
    @Column(name = "observacion", columnDefinition = "TEXT")
    private String observacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private CuadroDeObraEstado cuadroDeObraEstado;

    /**
     * Marca "nos presentamos" compartida por el equipo. NULL = sin marca. No se toca en
     * updateCuadro: se cambia solo por su endpoint dedicado (PATCH /{id}/presentacion).
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "presentacion", length = 3)
    private PresentacionMarca presentacion;

    // --- AUDITORIA DE AUTORIA ---
    // Las pobla AuditingEntityListener via UsuarioAuditorAware. Quedan NULL si no hay
    // usuario autenticado. No se exponen en los DTOs: son para reportes puntuales.
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
}
