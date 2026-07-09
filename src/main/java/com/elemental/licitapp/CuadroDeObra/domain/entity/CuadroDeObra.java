package com.elemental.licitapp.CuadroDeObra.domain.entity;

import com.elemental.licitapp.CuadroDeObra.domain.enums.CuadroDeObraEstado;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "cuadro_de_obra")
public class CuadroDeObra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "entidad_contratante", length = 500)
    private String entidadContratante;

    @Column(name = "num_proceso")
    private String numeroProceso;

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

}
