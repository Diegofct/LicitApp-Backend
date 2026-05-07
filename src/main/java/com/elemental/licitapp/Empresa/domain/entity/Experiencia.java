package com.elemental.licitapp.Empresa.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

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

    private String contratista;

    @Column(name = "entidad_contratante")
    private String entidadContratante;

    @Column(name = "valor_smmlv")
    private Double valorSMMLV;

    @Column(name = "porcentaje_participacion")
    private Double porcentajeParticipacion;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "codigos_unspsc", columnDefinition = "json")
    private List<String> codigosUNSPSC;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id")
    private Empresa empresa;
}
