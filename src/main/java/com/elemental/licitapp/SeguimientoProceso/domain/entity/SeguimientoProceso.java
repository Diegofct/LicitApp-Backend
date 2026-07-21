package com.elemental.licitapp.SeguimientoProceso.domain.entity;

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
@Table(name = "seguimiento_proceso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SeguimientoProceso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuadro_de_obra_id", nullable = false, unique = true)
    private Long cuadroDeObraId;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @OneToMany(mappedBy = "seguimiento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    @OrderBy("fechaEvento ASC, id ASC")
    private List<EventoSeguimiento> eventos = new ArrayList<>();

    // --- AUDITORIA DE AUTORIA ---
    // No hay @CreatedDate: fechaInicio ya es la fecha de creacion de la fila.
    // Ojo: el seguimiento se auto-inicializa al pasar el proceso a PRESENTADO, asi que
    // creadoPor sera quien hizo ese cambio de estado, no un alta manual.
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
