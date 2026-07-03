package com.elemental.licitapp.Empresa.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "empresas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nit;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    private String direccion;
    private String telefono;
    private String correo;

    @Column(name = "numero_proponente_ccb")
    private String numeroProponenteCcb;

    @Column(name = "tamano_empresa")
    private String tamanoEmpresa;

    // Flags explícitos que separan dos conceptos antes fusionados en el heurístico sobre
    // tamanoEmpresa: tamaño MiPyme vs. condición de proponente mujer. Afectan habilitantes
    // distintos (hoy: ambos otorgan el beneficio de +2 contratos de experiencia).
    @Column(name = "mipyme", nullable = false)
    private boolean mipyme;

    @Column(name = "proponente_mujer", nullable = false)
    private boolean proponenteMujer;

    @Column(name = "representante_legal")
    private String representanteLegal;

    @Column(name = "identificacion_representante_legal")
    private String identificacionRepresentanteLegal;

    @Column(name = "fecha_inscripcion")
    private java.time.LocalDate fechaInscripcion;

    @Column(name = "fecha_ultima_renovacion")
    private java.time.LocalDate fechaUltimaRenovacion;

    @OneToOne(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private IndicadoresFinancieros indicadores;

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Experiencia> experiencias = new ArrayList<>();

    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CapacidadResidualProponente> capacidadesResiduales = new ArrayList<>();
}
