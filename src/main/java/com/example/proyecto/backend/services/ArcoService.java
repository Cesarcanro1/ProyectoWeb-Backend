package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.proyecto.backend.dtos.ArcoDTO;
import com.example.proyecto.backend.entity.Arco;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ArcoRepository;

@Service
public class ArcoService {

    private final ArcoRepository repo;
    public ArcoService(ArcoRepository repo) { this.repo = repo; }

    // helpers
    private ArcoDTO toDTO(Arco a) {
        ArcoDTO dto = new ArcoDTO();
        dto.setId(a.getId());
        dto.setProcesoId(a.getProceso().getId()); // <- relación
        dto.setOrigenTipo(a.getOrigenTipo());
        dto.setOrigenId(a.getOrigenId());
        dto.setDestinoTipo(a.getDestinoTipo());
        dto.setDestinoId(a.getDestinoId());
        dto.setCondicion(a.getCondicion());
        return dto;
    }

    private Arco toEntity(ArcoDTO dto) {
        Arco a = new Arco();
        a.setId(dto.getId());

        // mapear proceso por id sin hit a BD
        Proceso p = new Proceso();
        p.setId(dto.getProcesoId());
        a.setProceso(p);

        a.setOrigenTipo(dto.getOrigenTipo());
        a.setOrigenId(dto.getOrigenId());
        a.setDestinoTipo(dto.getDestinoTipo());
        a.setDestinoId(dto.getDestinoId());
        a.setCondicion(dto.getCondicion());
        return a;
    }

    // CRUD
    public List<ArcoDTO> obtenerTodos() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public List<ArcoDTO> obtenerPorProceso(Long procesoId) {
        return repo.findAllByProceso_Id(procesoId).stream().map(this::toDTO).toList(); // <- cambio
    }

    public ArcoDTO obtenerPorId(Long id) {
        Arco a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));
        return toDTO(a);
    }

    public ArcoDTO crear(ArcoDTO dto) {
        Arco guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public ArcoDTO actualizar(Long id, ArcoDTO dto) {
        Arco a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));

        Proceso p = new Proceso();
        p.setId(dto.getProcesoId());
        a.setProceso(p);

        a.setOrigenTipo(dto.getOrigenTipo());
        a.setOrigenId(dto.getOrigenId());
        a.setDestinoTipo(dto.getDestinoTipo());
        a.setDestinoId(dto.getDestinoId());
        a.setCondicion(dto.getCondicion());
        return toDTO(repo.save(a));
    }

    // soft delete
    public void eliminar(Long id) {
        Arco a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));
        repo.delete(a);
    }
}
