package com.example.proyecto.backend.controllers;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyecto.backend.dtos.UsuarioDTO;
import com.example.proyecto.backend.services.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    // ADMIN y EDITOR pueden ver los usuarios
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public List<UsuarioDTO> obtenerTodos() {
        return service.obtenerTodos();
    }

    // TODOS pueden ver un usuario individual
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public UsuarioDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    // SOLO ADMIN puede crear usuarios
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioDTO crear(@RequestBody UsuarioDTO dto) {
        return service.crear(dto);
    }

    // ADMIN y EDITOR pueden actualizar 
    // (el Service ya se encarga de validar cambios prohibidos)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public UsuarioDTO actualizar(@PathVariable Long id, @RequestBody UsuarioDTO dto) {
        return service.actualizar(id, dto);
    }

    // SOLO ADMIN puede eliminar usuarios
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
