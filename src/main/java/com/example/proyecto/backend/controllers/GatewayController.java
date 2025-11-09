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

import com.example.proyecto.backend.dtos.GatewayDTO;
import com.example.proyecto.backend.services.GatewayService;

@RestController
@RequestMapping("/gateways")
public class GatewayController {

    private final GatewayService gatewayService;

    public GatewayController(GatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    // Obtener todos o por proceso ?procesoId=
    @GetMapping
    public List<GatewayDTO> obtenerTodos(@RequestParam(required = false) Long procesoId) {
        if (procesoId != null) {
            return gatewayService.obtenerPorProceso(procesoId);
        }
        return gatewayService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public GatewayDTO obtenerPorId(@PathVariable Long id) {
        return gatewayService.obtenerPorId(id);
    }

    @PostMapping
    public GatewayDTO crear(@RequestBody GatewayDTO gatewayDTO) {
        return gatewayService.crear(gatewayDTO);
    }

    @PutMapping("/{id}")
    public GatewayDTO actualizar(@PathVariable Long id, @RequestBody GatewayDTO gatewayDTO) {
        return gatewayService.actualizar(id, gatewayDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        gatewayService.eliminar(id);
    }
}
