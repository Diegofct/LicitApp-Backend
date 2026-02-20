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

    @Column(name = "entidad_contratante")
    private String entidadContratante;

    @Column(name = "num_proceso")
    private String numeroProceso;

    @Column(name = "descripcion_objeto")
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

    @Column(name = "experiencia")
    private String experiencia;

    @Column(name = "plazo")
    private String plazo;

    @Column(name = "anticipo")
    private String anticipo;

    @Column(name = "observacion")
    private String observacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private CuadroDeObraEstado cuadroDeObraEstado;

}
