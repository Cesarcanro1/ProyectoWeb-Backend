package com.example.proyecto.backend.entity;

import java.io.Serializable;
import java.util.List;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "proceso")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 0") // solo activos
@SQLDelete(sql = "UPDATE proceso SET status = 1 WHERE id = ?") // soft delete
public class Proceso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "empresa_id", nullable = false, foreignKey = @ForeignKey(name = "fk_proceso_empresa"))
    private Empresa empresa;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String categoria; // puede ser "Administrativo", "Operativo", etc.

    @Column(nullable = false)
    private String estado; // ejemplo: "Borrador" o "Publicado"

    @Column(nullable = false)
    private int status = 0; // 0 = activo, 1 = eliminado

    // Relaciones
    @OneToMany(mappedBy = "proceso", fetch = FetchType.LAZY)
    private List<Actividad> actividades;

    @OneToMany(mappedBy = "proceso", fetch = FetchType.LAZY)
    private List<Gateway> gateways;

    @OneToMany(mappedBy = "proceso", fetch = FetchType.LAZY)
    private List<Arco> arcos;
}
