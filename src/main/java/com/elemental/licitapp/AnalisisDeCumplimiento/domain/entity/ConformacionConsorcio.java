package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conformacion_consorcio")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConformacionConsorcio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cuadro_de_obra_id", nullable = false, unique = true)
    private Long cuadroDeObraId;

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
}
