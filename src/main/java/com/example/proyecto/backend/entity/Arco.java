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
@Table(name = "arco")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 0") // solo activos
@SQLDelete(sql = "UPDATE arco SET status = 1 WHERE id = ?") // soft delete
public class Arco implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "proceso_id", nullable = false, foreignKey = @ForeignKey(name = "fk_arco_proceso"))
    private Proceso proceso;

    // nodo origen: tipo = "Actividad" o "Gateway"
    @Column(nullable = false)
    private String origenTipo;

    @Column(nullable = false)
    private Long origenId;

    // nodo destino: tipo = "Actividad" o "Gateway"
    @Column(nullable = false)
    private String destinoTipo;

    @Column(nullable = false)
    private Long destinoId;

    // condición opcional para ramas (ej: "monto > 1000")
    @Column(nullable = true)
    private String condicion;

    @Column(nullable = false)
    private int status = 0; // 0 = activo, 1 = eliminado
}
