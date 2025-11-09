package com.example.proyecto.backend.services;

import com.example.proyecto.backend.dtos.GatewayDTO;
import com.example.proyecto.backend.entity.Gateway;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.GatewayRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GatewayService {

    private final GatewayRepository repo;
    public GatewayService(GatewayRepository repo) { this.repo = repo; }

    // helpers
    private GatewayDTO toDTO(Gateway g) {
        GatewayDTO dto = new GatewayDTO();
        dto.setId(g.getId());
        dto.setProcesoId(g.getProceso().getId()); // <- usa la relación
        dto.setNombre(g.getNombre());
        dto.setTipo(g.getTipo());
        dto.setDescripcion(g.getDescripcion());
        return dto;
    }

    private Gateway toEntity(GatewayDTO dto) {
        Gateway g = new Gateway();
        g.setId(dto.getId());

        Proceso p = new Proceso();       // map sin ir a BD
        p.setId(dto.getProcesoId());
        g.setProceso(p);

        g.setNombre(dto.getNombre());
        g.setTipo(dto.getTipo());
        g.setDescripcion(dto.getDescripcion());
        return g;
    }

    // CRUD
    public List<GatewayDTO> obtenerTodos() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public List<GatewayDTO> obtenerPorProceso(Long procesoId) {
        return repo.findAllByProceso_Id(procesoId).stream().map(this::toDTO).toList(); // <- cambio
    }

    public GatewayDTO obtenerPorId(Long id) {
        Gateway g = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Gateway no encontrado"));
        return toDTO(g);
    }

    public GatewayDTO crear(GatewayDTO dto) {
        Gateway guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public GatewayDTO actualizar(Long id, GatewayDTO dto) {
        Gateway g = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Gateway no encontrado"));

        Proceso p = new Proceso();
        p.setId(dto.getProcesoId());
        g.setProceso(p);

        g.setNombre(dto.getNombre());
        g.setTipo(dto.getTipo());
        g.setDescripcion(dto.getDescripcion());
        return toDTO(repo.save(g));
    }

    // soft delete
    public void eliminar(Long id) {
        Gateway g = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Gateway no encontrado"));
        repo.delete(g);
    }
}
