package com.example.proyecto.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String nit;
    private String correoContacto;
}
