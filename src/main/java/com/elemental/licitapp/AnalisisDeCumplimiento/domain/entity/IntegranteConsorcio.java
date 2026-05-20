package com.elemental.licitapp.AnalisisDeCumplimiento.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "integrante_consorcio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IntegranteConsorcio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conformacion_id", nullable = false)
    private ConformacionConsorcio conformacion;

    @Column(name = "empresa_id", nullable = false)
    private Long empresaId;

    @Column(name = "porcentaje_participacion", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeParticipacion;
}
