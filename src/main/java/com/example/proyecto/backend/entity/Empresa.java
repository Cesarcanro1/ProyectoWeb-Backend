package com.example.proyecto.backend.entity;

import java.io.Serializable;
import java.util.List;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "empresa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "status = 0") // solo trae las activas
@SQLDelete(sql = "UPDATE empresa SET status = 1 WHERE id = ?") // cuando hace delete cambia status
public class Empresa implements Serializable{

    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String nit;

    @Column(nullable = false, unique = true)
    private String correoContacto;

    @Column(nullable = false)
    private int status = 0; // 0 = activo, 1 = eliminado

    // Relaciones
    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<Proceso> procesos;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<Usuario> usuarios;

    @OneToMany(mappedBy = "empresa", fetch = FetchType.LAZY)
    private List<RolDeProceso> rolesDeProceso;
}
