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

import com.example.proyecto.backend.dtos.ProcesoDTO;
import com.example.proyecto.backend.services.ProcesoService;

@RestController
@RequestMapping("/api/procesos")
public class ProcesoController {

    private final ProcesoService service;

    public ProcesoController(ProcesoService service) {
        this.service = service;
    }

    
    // CONSULTAS (HU-07)
   
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public List<ProcesoDTO> obtenerTodos() {
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public ProcesoDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    // CREAR (HU-04)
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ProcesoDTO crear(@RequestBody ProcesoDTO dto) {
        return service.crear(dto);
    }

    
    // ACTUALIZAR (HU-05)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ProcesoDTO actualizar(@PathVariable Long id, @RequestBody ProcesoDTO dto) {
        return service.actualizar(id, dto);
    }


    // ELIMINAR (HU-06)
   
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
