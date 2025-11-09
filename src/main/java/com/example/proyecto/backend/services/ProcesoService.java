package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.proyecto.backend.dtos.ProcesoDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ProcesoRepository;

@Service
public class ProcesoService {

    private final ProcesoRepository repo;
    public ProcesoService(ProcesoRepository repo) { this.repo = repo; }

    // helpers
    private ProcesoDTO toDTO(Proceso p) {
        ProcesoDTO dto = new ProcesoDTO();
        dto.setId(p.getId());
        dto.setEmpresaId(p.getEmpresa().getId()); // <- relación
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setCategoria(p.getCategoria());
        dto.setEstado(p.getEstado());
        return dto;
    }

    private Proceso toEntity(ProcesoDTO dto) {
        Proceso p = new Proceso();
        p.setId(dto.getId());

        // mapear empresa por id sin ir a BD
        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId());
        p.setEmpresa(e);

        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setCategoria(dto.getCategoria());
        p.setEstado(dto.getEstado());
        return p;
    }

    // CRUD
    public List<ProcesoDTO> obtenerTodos() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public List<ProcesoDTO> obtenerPorEmpresa(Long empresaId) {
        return repo.findAllByEmpresa_Id(empresaId).stream().map(this::toDTO).toList(); // <- cambio
    }

    public ProcesoDTO obtenerPorId(Long id) {
        Proceso p = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));
        return toDTO(p);
    }

    public ProcesoDTO crear(ProcesoDTO dto) {
        Proceso guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public ProcesoDTO actualizar(Long id, ProcesoDTO dto) {
        Proceso p = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));

        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId());
        p.setEmpresa(e);

        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setCategoria(dto.getCategoria());
        p.setEstado(dto.getEstado());
        return toDTO(repo.save(p));
    }

    // soft delete
    public void eliminar(Long id) {
        Proceso p = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));
        repo.delete(p);
    }
}
