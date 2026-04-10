package com.elemental.licitapp.Empresa.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "experiencia_empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Experiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_contrato")
    private String numeroContrato;

    private String contratante;
    
    @Column(columnDefinition = "TEXT")
    private String objeto;

    @Column(name = "valor_pesos")
    private Double valorPesos;

    @Column(name = "valor_smmlv")
    private Double valorSMMLV;

    @Column(name = "fecha_terminacion")
    private LocalDate fechaTerminacion;

    @Column(name = "codigos_unspsc")
    private String codigosUNSPSC;

    @Column(name = "porcentaje_participacion_consorcio")
    private Double porcentajeParticipacionConsorcio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    @JsonBackReference
    private Empresa empresa;
}
