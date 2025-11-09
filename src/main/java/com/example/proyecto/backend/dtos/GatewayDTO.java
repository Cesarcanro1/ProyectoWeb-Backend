package com.example.proyecto.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GatewayDTO {
    private Long id;
    private Long procesoId;
    private String nombre;
    private String tipo;
    private String descripcion;
}
