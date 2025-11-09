package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.proyecto.backend.dtos.ActividadDTO;
import com.example.proyecto.backend.entity.Actividad;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ActividadRepository;

@Service
public class ActividadService {

    private final ActividadRepository repo;
    public ActividadService(ActividadRepository repo) { this.repo = repo; }

    // helpers
    private ActividadDTO toDTO(Actividad a) {
        ActividadDTO dto = new ActividadDTO();
        dto.setId(a.getId());
        dto.setProcesoId(a.getProceso().getId()); // <- relación
        dto.setNombre(a.getNombre());
        dto.setTipo(a.getTipo());
        dto.setDescripcion(a.getDescripcion());
        dto.setRolResponsable(a.getRolResponsable());
        return dto;
    }

    private Actividad toEntity(ActividadDTO dto) {
        Actividad a = new Actividad();
        a.setId(dto.getId());

        // mapear proceso por id sin ir a BD
        Proceso p = new Proceso();
        p.setId(dto.getProcesoId());
        a.setProceso(p);

        a.setNombre(dto.getNombre());
        a.setTipo(dto.getTipo());
        a.setDescripcion(dto.getDescripcion());
        a.setRolResponsable(dto.getRolResponsable());
        return a;
    }

    // CRUD
    public List<ActividadDTO> obtenerTodos() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public List<ActividadDTO> obtenerPorProceso(Long procesoId) {
        return repo.findAllByProceso_Id(procesoId).stream().map(this::toDTO).toList(); // <- cambio
    }

    public ActividadDTO obtenerPorId(Long id) {
        Actividad a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));
        return toDTO(a);
    }

    public ActividadDTO crear(ActividadDTO dto) {
        Actividad guardada = repo.save(toEntity(dto));
        return toDTO(guardada);
    }

    public ActividadDTO actualizar(Long id, ActividadDTO dto) {
        Actividad a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));

        // actualizar relación proceso
        Proceso p = new Proceso();
        p.setId(dto.getProcesoId());
        a.setProceso(p);

        a.setNombre(dto.getNombre());
        a.setTipo(dto.getTipo());
        a.setDescripcion(dto.getDescripcion());
        a.setRolResponsable(dto.getRolResponsable());
        return toDTO(repo.save(a));
    }

    // soft delete
    public void eliminar(Long id) {
        Actividad a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));
        repo.delete(a);
    }
}
