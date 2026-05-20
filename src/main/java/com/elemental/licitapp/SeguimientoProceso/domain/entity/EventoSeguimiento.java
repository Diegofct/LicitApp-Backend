package com.elemental.licitapp.SeguimientoProceso.domain.entity;

import com.elemental.licitapp.SeguimientoProceso.domain.enums.TipoEventoSeguimiento;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "evento_seguimiento")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventoSeguimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seguimiento_id", nullable = false)
    private SeguimientoProceso seguimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 64)
    private TipoEventoSeguimiento tipo;

    @Column(name = "fecha_evento", nullable = false)
    private LocalDateTime fechaEvento;

    @Lob
    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;
}
