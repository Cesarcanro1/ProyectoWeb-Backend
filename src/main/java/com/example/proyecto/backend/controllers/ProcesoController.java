package com.example.proyecto.backend.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyecto.backend.dtos.ProcesoDTO;
import com.example.proyecto.backend.services.ProcesoService;

@RestController
@RequestMapping("/procesos")
public class ProcesoController {

    private final ProcesoService procesoService;

    public ProcesoController(ProcesoService procesoService) {
        this.procesoService = procesoService;
    }

    // Obtener todos o por empresa ?empresaId=
    @GetMapping
    public List<ProcesoDTO> obtenerTodos(@RequestParam(required = false) Long empresaId) {
        if (empresaId != null) {
            return procesoService.obtenerPorEmpresa(empresaId);
        }
        return procesoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ProcesoDTO obtenerPorId(@PathVariable Long id) {
        return procesoService.obtenerPorId(id);
    }

    @PostMapping
    public ProcesoDTO crear(@RequestBody ProcesoDTO procesoDTO) {
        return procesoService.crear(procesoDTO);
    }

    @PutMapping("/{id}")
    public ProcesoDTO actualizar(@PathVariable Long id, @RequestBody ProcesoDTO procesoDTO) {
        return procesoService.actualizar(id, procesoDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        procesoService.eliminar(id);
    }
}
