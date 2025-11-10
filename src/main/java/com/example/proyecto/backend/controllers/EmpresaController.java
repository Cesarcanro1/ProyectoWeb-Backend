package com.example.proyecto.backend.controllers;

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

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Obtener la empresa asociada al usuario logueado
    @GetMapping("/mi-empresa")
    public EmpresaDTO obtenerMiEmpresa() {
        return empresaService.obtenerMiEmpresa();
    }

    // Bloquea los demás endpoints por ahora
    @GetMapping
    public void obtenerTodas() {
        throw new RuntimeException("No autorizado");
    }

    @GetMapping("/{id}")
    public EmpresaDTO obtenerPorId(@PathVariable Long id) {
        return empresaService.obtenerPorId(id);
    }

    @PostMapping
    public void crear(@RequestBody EmpresaDTO empresaDTO) {
        throw new RuntimeException("No autorizado");
    }

    @PutMapping("/{id}")
    public EmpresaDTO actualizar(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        return empresaService.actualizar(id, empresaDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        throw new RuntimeException("No autorizado");
    }
}