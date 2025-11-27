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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyecto.backend.dtos.ActividadDTO;
import com.example.proyecto.backend.services.ActividadService;

@RestController
@RequestMapping("/api/actividades")
public class ActividadController {

    private final ActividadService service;

    public ActividadController(ActividadService service) {
        this.service = service;
    }

   
    // CONSULTAR (HU-07)
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public List<ActividadDTO> obtenerTodos(@RequestParam(required = false) Long procesoId) {
        if (procesoId != null) return service.obtenerPorProceso(procesoId);
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public ActividadDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    // CREAR (HU-08)
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ActividadDTO crear(@RequestBody ActividadDTO dto) {
        return service.crear(dto);
    }

    
    // ACTUALIZAR (HU-09)
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ActividadDTO actualizar(@PathVariable Long id, @RequestBody ActividadDTO dto) {
        return service.actualizar(id, dto);
    }

    
    // ELIMINAR (HU-10)
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
