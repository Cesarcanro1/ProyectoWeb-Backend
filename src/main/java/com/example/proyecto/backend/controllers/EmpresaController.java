package com.example.proyecto.backend.controllers;

import java.util.List;

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
@RequestMapping("/empresas")
public class EmpresaController {
    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    // Obtener todas las empresas activas
    @GetMapping
    public List<EmpresaDTO> obtenerTodas() {
        return empresaService.obtenerTodos();
    }

    // Obtener una empresa por ID
    @GetMapping("/{id}")
    public EmpresaDTO obtenerPorId(@PathVariable Long id) {
        return empresaService.obtenerPorId(id);
    }

    // Crear una nueva empresa
    @PostMapping
    public EmpresaDTO crear(@RequestBody EmpresaDTO empresaDTO) {
        return empresaService.crear(empresaDTO);
    }

    // Actualizar una empresa existente
    @PutMapping("/{id}")
    public EmpresaDTO actualizar(@PathVariable Long id, @RequestBody EmpresaDTO empresaDTO) {
        return empresaService.actualizar(id, empresaDTO);
    }

    // Eliminar lógicamente una empresa (soft delete)
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        empresaService.eliminar(id);
    }
}
