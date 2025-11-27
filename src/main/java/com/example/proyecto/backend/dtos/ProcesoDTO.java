package com.example.proyecto.backend.dtos;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ProcesoDTO {
    private Long id;
    private Long empresaId;
    private String nombre;
    private String descripcion;
    private String categoria;
    private String estado;
}
