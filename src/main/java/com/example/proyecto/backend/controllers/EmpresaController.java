package com.example.proyecto.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyecto.backend.dtos.EmpresaDTO;
import com.example.proyecto.backend.services.EmpresaService;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaService service;

    public EmpresaController(EmpresaService service) {
        this.service = service;
    }

  
    // REGISTRO DE EMPRESA (PUBLIC)
    @PostMapping("/registrar")
    public EmpresaDTO registrar(@RequestBody EmpresaDTO dto) {
        return service.registrarEmpresa(dto);
    }

    
    // DATOS DE MI EMPRESA
    // Para cualquier usuario autenticado de la empresa.
    @GetMapping("/mi-empresa")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public EmpresaDTO obtenerMiEmpresa() {
        return service.obtenerMiEmpresa();
    }

    
    // VER EMPRESA POR ID
    // Solo si es tu propia empresa (service valida esto también).
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public EmpresaDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    // ACTUALIZAR MI EMPRESA
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public EmpresaDTO actualizar(@PathVariable Long id, @RequestBody EmpresaDTO dto) {
        return service.actualizar(id, dto);
    }

    
    // ELIMINACIÓN BLOQUEADA
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        throw new RuntimeException("Eliminar empresa no está permitido.");
    }
}
