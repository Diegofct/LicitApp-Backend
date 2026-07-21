package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conformacion_consorcio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class ConformacionConsorcio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuadro_de_obra_id", nullable = false, unique = true)
    private Long cuadroDeObraId;

    // Nombre del proponente plural (ej. "Consorcio Vías del Norte"). Obligatorio para
    // CONSORCIO/UNION_TEMPORAL; puede ser null para INDIVIDUAL (se valida en el AppService).
    @Column(name = "nombre", length = 255)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_participacion", nullable = false, length = 32)
    private TipoParticipacion tipoParticipacion;

    @Column(name = "fecha_conformacion", nullable = false)
    private LocalDateTime fechaConformacion;

    @Lob
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @OneToMany(mappedBy = "conformacion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<IntegranteConsorcio> integrantes = new ArrayList<>();

    // --- AUDITORIA DE AUTORIA ---
    // No hay @CreatedDate: fechaConformacion ya es la fecha de creacion de la fila
    // (la setea el AppService al conformar), asi que duplicarla solo daria dos fuentes
    // de verdad para el mismo dato.
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
