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

import com.example.proyecto.backend.dtos.RolDeProcesoDTO;
import com.example.proyecto.backend.services.RolDeProcesoService;

@RestController
@RequestMapping("/api/roles-de-proceso")
public class RolDeProcesoController {

    private final RolDeProcesoService service;

    public RolDeProcesoController(RolDeProcesoService service) {
        this.service = service;
    }

    // CONSULTAR (ADMIN, EDITOR, VIEWER)
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public List<RolDeProcesoDTO> obtenerTodos(@RequestParam(required = false) Long empresaId) {
        if (empresaId != null) return service.obtenerPorEmpresa(empresaId);
        return service.obtenerTodos();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','EDITOR','VIEWER')")
    public RolDeProcesoDTO obtenerPorId(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

   
    // CREAR (SOLO ADMIN)
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public RolDeProcesoDTO crear(@RequestBody RolDeProcesoDTO dto) {
        return service.crear(dto);
    }

   
    // ACTUALIZAR (SOLO ADMIN)
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RolDeProcesoDTO actualizar(@PathVariable Long id, @RequestBody RolDeProcesoDTO dto) {
        return service.actualizar(id, dto);
    }

    // ELIMINAR (SOLO ADMIN)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void eliminar(@PathVariable Long id) {
        service.eliminar(id);
    }
}
