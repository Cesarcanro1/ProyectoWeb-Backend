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

import com.example.proyecto.backend.dtos.RolDeProcesoDTO;
import com.example.proyecto.backend.services.RolDeProcesoService;

@RestController
@RequestMapping("/api/roles-de-proceso")
public class RolDeProcesoController {

    private final RolDeProcesoService rolDeProcesoService;

    public RolDeProcesoController(RolDeProcesoService rolDeProcesoService) {
        this.rolDeProcesoService = rolDeProcesoService;
    }

    // Obtener todos o filtrar por empresa ?empresaId=
    @GetMapping
    public List<RolDeProcesoDTO> obtenerTodos(@RequestParam(required = false) Long empresaId) {
        if (empresaId != null) {
            return rolDeProcesoService.obtenerPorEmpresa(empresaId);
        }
        return rolDeProcesoService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public RolDeProcesoDTO obtenerPorId(@PathVariable Long id) {
        return rolDeProcesoService.obtenerPorId(id);
    }

    @PostMapping
    public RolDeProcesoDTO crear(@RequestBody RolDeProcesoDTO rolDTO) {
        return rolDeProcesoService.crear(rolDTO);
    }

    @PutMapping("/{id}")
    public RolDeProcesoDTO actualizar(@PathVariable Long id, @RequestBody RolDeProcesoDTO rolDTO) {
        return rolDeProcesoService.actualizar(id, rolDTO);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        rolDeProcesoService.eliminar(id);
    }
}
