package com.elemental.licitapp.Licitaciones.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Table(name = "licitaciones")
public class Licitacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_del_proceso", unique = true, length = 50)
    private String idDelProceso;

    @Column(name = "entidad", length = 100)
    private String entidad;

    @Column(name = "objeto", columnDefinition = "TEXT")
    private String objeto;

    @Column(name = "cuantia", columnDefinition = "DECIMAL(20,2)")
    private BigDecimal cuantia;

    @Column(name = "modalidad")
    private String modalidad;

    @Column(name = "numero")
    private String numero; // → referencia_del_proceso

    @Column(name = "estado")
    private String estado;

    @Column(name = "fecha_publicacion")
    private LocalDateTime fechaPublicacion; // → fecha_de_publicacion_del

    @Column(name = "ubicacion")
    private String ubicacion; // → Combinar departamento_entidad + ciudad_entidad

    @Column(name = "url_secop")
    private String urlSecop; // → urlproceso

    @Column(name = "codigo_unpspc")
    private String codigoUnpspc;

    @Column(name = "consorcio_id")
    private Long consorcioId;

}
