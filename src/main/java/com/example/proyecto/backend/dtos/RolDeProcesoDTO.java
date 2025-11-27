package com.example.proyecto.backend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class RolDeProcesoDTO {
    private Long id;
    private Long empresaId;
    private String nombre;
    private String descripcion;
}
