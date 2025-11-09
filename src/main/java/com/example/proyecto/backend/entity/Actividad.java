package com.example.proyecto.backend.entity;

import java.io.Serializable;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "actividad")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 0") // solo activas
@SQLDelete(sql = "UPDATE actividad SET status = 1 WHERE id = ?") // soft delete
public class Actividad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proceso_id", nullable = false, foreignKey = @ForeignKey(name = "fk_actividad_proceso"))
    private Proceso proceso;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String tipo; // p.ej. "Tarea", "Subproceso", etc.

    @Column(nullable = false)
    private String descripcion;

    // quién la ejecuta (rol simple por ahora)
    @Column(nullable = false)
    private String rolResponsable;

    @Column(nullable = false)
    private int status = 0; // 0 = activo, 1 = eliminado
}
