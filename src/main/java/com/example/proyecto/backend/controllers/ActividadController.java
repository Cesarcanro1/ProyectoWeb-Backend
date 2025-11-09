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

import com.example.proyecto.backend.dtos.ActividadDTO;
import com.example.proyecto.backend.services.ActividadService;

@RestController
@RequestMapping("/actividades")
public class ActividadController {

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    // Obtener todas o filtrar por proceso ?procesoId=
    @GetMapping
    public List<ActividadDTO> obtenerTodos(@RequestParam(required = false) Long procesoId) {
        if (procesoId != null) {
            return actividadService.obtenerPorProceso(procesoId);
        }
        return actividadService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ActividadDTO obtenerPorId(@PathVariable Long id) {
        return actividadService.obtenerPorId(id);
    }

    @PostMapping
    public ActividadDTO crear(@RequestBody ActividadDTO actividadDTO) {
        return actividadService.crear(actividadDTO);
    }

    @PutMapping("/{id}")
    public ActividadDTO actualizar(@PathVariable Long id, @RequestBody ActividadDTO actividadDTO) {
        return actividadService.actualizar(id, actividadDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        actividadService.eliminar(id);
    }
}
