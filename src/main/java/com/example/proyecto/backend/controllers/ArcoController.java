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

import com.example.proyecto.backend.dtos.ArcoDTO;
import com.example.proyecto.backend.services.ArcoService;

@RestController
@RequestMapping("/api/arcos")
public class ArcoController {

    private final ArcoService service;

    public ArcoController(ArcoService service) {
        this.service = service;
    }

   
    // CONSULTAR (Roles: ADMIN, EDITOR, VIEWER)
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public List<ArcoDTO> obtenerTodos(@RequestParam(required = false) Long procesoId) {
        if (procesoId != null) {
            return service.obtenerPorProceso(procesoId);
        }
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public ArcoDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    
    // CREAR (Roles: ADMIN, EDITOR)
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ArcoDTO crear(@RequestBody ArcoDTO dto) {
        return service.crear(dto);
    }

   
    // ACTUALIZAR (Roles: ADMIN, EDITOR)
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR')")
    public ArcoDTO actualizar(@PathVariable Long id, @RequestBody ArcoDTO dto) {
        return service.actualizar(id, dto);
    }

    
    // ELIMINAR (Roles: SOLO ADMIN)
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
