package com.example.proyecto.backend.services;

import com.example.proyecto.backend.dtos.EmpresaDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.repository.EmpresaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmpresaService {

    private final EmpresaRepository repo;
    public EmpresaService(EmpresaRepository repo) { this.repo = repo; }

    // helpers
    private EmpresaDTO toDTO(Empresa e) {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setNit(e.getNit());
        dto.setCorreoContacto(e.getCorreoContacto());
        return dto;
    }

    private Empresa toEntity(EmpresaDTO dto) {
        Empresa e = new Empresa();
        e.setId(dto.getId());
        e.setNombre(dto.getNombre());
        e.setNit(dto.getNit());
        e.setCorreoContacto(dto.getCorreoContacto());
        return e;
    }

    // CRUD básico
    public List<EmpresaDTO> obtenerTodos() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public EmpresaDTO obtenerPorId(Long id) {
        Empresa e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        return toDTO(e);
    }

    public EmpresaDTO crear(EmpresaDTO dto) {
        repo.findByNit(dto.getNit()).ifPresent(x -> { throw new IllegalArgumentException("NIT ya existe"); });
        Empresa guardada = repo.save(toEntity(dto));
        return toDTO(guardada);
    }

    public EmpresaDTO actualizar(Long id, EmpresaDTO dto) {
        Empresa e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        e.setNombre(dto.getNombre());
        e.setNit(dto.getNit());
        e.setCorreoContacto(dto.getCorreoContacto());
        return toDTO(repo.save(e));
    }

    // soft delete -> dispara @SQLDelete (status=1)
    public void eliminar(Long id) {
        Empresa e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        repo.delete(e);
    }
}
