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

import com.example.proyecto.backend.dtos.ArcoDTO;
import com.example.proyecto.backend.services.ArcoService;

@RestController
@RequestMapping("/arcos")
public class ArcoController {

    private final ArcoService arcoService;

    public ArcoController(ArcoService arcoService) {
        this.arcoService = arcoService;
    }

    // Obtener todos o por proceso ?procesoId=
    @GetMapping
    public List<ArcoDTO> obtenerTodos(@RequestParam(required = false) Long procesoId) {
        if (procesoId != null) {
            return arcoService.obtenerPorProceso(procesoId);
        }
        return arcoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ArcoDTO obtenerPorId(@PathVariable Long id) {
        return arcoService.obtenerPorId(id);
    }

    @PostMapping
    public ArcoDTO crear(@RequestBody ArcoDTO arcoDTO) {
        return arcoService.crear(arcoDTO);
    }

    @PutMapping("/{id}")
    public ArcoDTO actualizar(@PathVariable Long id, @RequestBody ArcoDTO arcoDTO) {
        return arcoService.actualizar(id, arcoDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        arcoService.eliminar(id);
    }
}
