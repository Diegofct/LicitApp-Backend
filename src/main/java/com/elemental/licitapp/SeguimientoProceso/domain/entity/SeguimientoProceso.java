package com.elemental.licitapp.SeguimientoProceso.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "seguimiento_proceso")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
}
