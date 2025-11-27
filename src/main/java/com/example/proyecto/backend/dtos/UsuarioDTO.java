package com.example.proyecto.backend.dtos;

import com.example.proyecto.backend.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDTO {

    private Long id;
    private Long empresaId;
    private String nombres;
    private String apellidos;
    private String email;
    private String password; 
    private Role role;        // ADMIN, EDITOR, VIEWER
}
