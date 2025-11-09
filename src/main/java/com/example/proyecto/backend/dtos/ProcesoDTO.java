package com.example.proyecto.backend.dtos;

import lombok.Getter;
import lombok.Setter;

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
