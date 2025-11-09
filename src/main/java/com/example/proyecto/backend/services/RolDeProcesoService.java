package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.proyecto.backend.dtos.RolDeProcesoDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.RolDeProceso;
import com.example.proyecto.backend.repository.RolDeProcesoRepository;

@Service
public class RolDeProcesoService {

    private final RolDeProcesoRepository repo;
    public RolDeProcesoService(RolDeProcesoRepository repo) { this.repo = repo; }

    // helpers
    private RolDeProcesoDTO toDTO(RolDeProceso r) {
        RolDeProcesoDTO dto = new RolDeProcesoDTO();
        dto.setId(r.getId());
        dto.setEmpresaId(r.getEmpresa().getId()); // <- relación
        dto.setNombre(r.getNombre());
        dto.setDescripcion(r.getDescripcion());
        return dto;
    }

    private RolDeProceso toEntity(RolDeProcesoDTO dto) {
        RolDeProceso r = new RolDeProceso();
        r.setId(dto.getId());

        Empresa e = new Empresa();      // map por id sin ir a BD
        e.setId(dto.getEmpresaId());
        r.setEmpresa(e);

        r.setNombre(dto.getNombre());
        r.setDescripcion(dto.getDescripcion());
        return r;
    }

    // CRUD
    public List<RolDeProcesoDTO> obtenerTodos() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public List<RolDeProcesoDTO> obtenerPorEmpresa(Long empresaId) {
        return repo.findAllByEmpresa_Id(empresaId).stream().map(this::toDTO).toList(); // <- cambio
    }

    public RolDeProcesoDTO obtenerPorId(Long id) {
        RolDeProceso r = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));
        return toDTO(r);
    }

    public RolDeProcesoDTO crear(RolDeProcesoDTO dto) {
        RolDeProceso guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public RolDeProcesoDTO actualizar(Long id, RolDeProcesoDTO dto) {
        RolDeProceso r = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));

        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId());
        r.setEmpresa(e);

        r.setNombre(dto.getNombre());
        r.setDescripcion(dto.getDescripcion());
        return toDTO(repo.save(r));
    }

    // soft delete
    public void eliminar(Long id) {
        RolDeProceso r = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));
        repo.delete(r);
    }
}
