package com.example.proyecto.backend.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class EmpresaDTO {
    private Long id;
    private String nombre;
    private String nit;
    private String correoContacto;

    // Se usa solo para registro inicial de empresa
    private UsuarioDTO admin;
}
