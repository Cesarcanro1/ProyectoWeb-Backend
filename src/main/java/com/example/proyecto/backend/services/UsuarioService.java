package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.example.proyecto.backend.dtos.UsuarioDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Usuario;
import com.example.proyecto.backend.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository repo;
    public UsuarioService(UsuarioRepository repo) { this.repo = repo; }

    // helpers
    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setEmpresaId(u.getEmpresa().getId()); // <- relación
        dto.setNombres(u.getNombres());
        dto.setApellidos(u.getApellidos());
        dto.setEmail(u.getEmail());
        dto.setPassword(u.getPassword()); // si no quieres exponerla, bórrala del DTO
        return dto;
    }

    private Usuario toEntity(UsuarioDTO dto) {
        Usuario u = new Usuario();
        u.setId(dto.getId());

        Empresa e = new Empresa();   // map por id sin ir a BD
        e.setId(dto.getEmpresaId());
        u.setEmpresa(e);

        u.setNombres(dto.getNombres());
        u.setApellidos(dto.getApellidos());
        u.setEmail(dto.getEmail());
        u.setPassword(dto.getPassword());
        return u;
    }

    // CRUD básico
    public List<UsuarioDTO> obtenerTodos() {
        return repo.findAll().stream().map(this::toDTO).toList();
    }

    public List<UsuarioDTO> obtenerPorEmpresa(Long empresaId) {
        return repo.findAllByEmpresa_Id(empresaId).stream().map(this::toDTO).toList(); // <- cambio
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario u = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        return toDTO(u);
    }

    public UsuarioDTO crear(UsuarioDTO dto) {
        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }
        Usuario guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario u = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(u.getEmail()) && repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }

        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId());
        u.setEmpresa(e);

        u.setNombres(dto.getNombres());
        u.setApellidos(dto.getApellidos());
        u.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(dto.getPassword());
        }
        return toDTO(repo.save(u));
    }

    // soft delete -> dispara @SQLDelete (status=1)
    public void eliminar(Long id) {
        Usuario u = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        repo.delete(u);
    }
}
