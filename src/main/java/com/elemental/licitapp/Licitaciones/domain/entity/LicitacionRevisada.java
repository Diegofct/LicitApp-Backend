package com.elemental.licitapp.Licitaciones.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Marca "Revisado" de una licitacion de SECOP II. Es un conjunto: la existencia de la fila
 * significa que la licitacion (identificada por {@code idDelProceso}) fue revisada por el
 * equipo. La marca es compartida; {@code creadoPor}/{@code fechaCreacion} solo registran
 * quien la creo (no se exponen ni filtran por autor).
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "licitacion_revisada")
@EntityListeners(AuditingEntityListener.class)
public class LicitacionRevisada {

    @Id
    @Column(name = "id_del_proceso")
    private String idDelProceso;

    @CreatedDate
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @CreatedBy
    @Column(name = "creado_por", updatable = false)
    private Long creadoPor;

    public LicitacionRevisada(String idDelProceso) {
        this.idDelProceso = idDelProceso;
    }
}
