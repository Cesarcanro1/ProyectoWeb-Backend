package com.example.proyecto.backend.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioDTO {
    private Long id;
    private Long empresaId;
    private String nombres;
    private String apellidos;
    private String email;
    private String password;
}
