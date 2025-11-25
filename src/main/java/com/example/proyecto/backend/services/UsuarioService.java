package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.UsuarioDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Usuario;
import com.example.proyecto.backend.repository.UsuarioRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repo;
    private final SecurityUtils securityUtils;
    private final PasswordEncoder passwordEncoder;

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setEmpresaId(u.getEmpresa().getId());
        dto.setNombres(u.getNombres());
        dto.setApellidos(u.getApellidos());
        dto.setEmail(u.getEmail());
        return dto;
    }

    private Usuario toEntity(UsuarioDTO dto, boolean encodePassword) {
        Usuario u = new Usuario();
        u.setId(dto.getId());

        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId()); 
        u.setEmpresa(e);

        u.setNombres(dto.getNombres());
        u.setApellidos(dto.getApellidos());
        u.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(
                    encodePassword ? passwordEncoder.encode(dto.getPassword()) : dto.getPassword()
            );
        }

        return u;
    }

    // ===================== QUERIES =====================

    public List<UsuarioDTO> obtenerTodos() {
        Long currentCompanyId = securityUtils.currentCompanyId();
        if (currentCompanyId == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin empresa asociada");
        return repo.findAllByEmpresa_Id(currentCompanyId)
                   .stream()
                   .map(this::toDTO)
                   .toList();
    }

    public List<UsuarioDTO> obtenerPorEmpresa(Long empresaId) {
        validarAccesoEmpresa(empresaId);
        return repo.findAllByEmpresa_Id(empresaId)
                   .stream()
                   .map(this::toDTO)
                   .toList();
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario u = repo.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        validarAccesoEmpresa(u.getEmpresa().getId());
        return toDTO(u);
    }

    // ===================== CREAR / ACTUALIZAR / ELIMINAR =====================

    // Crear usuario desde un contexto AUTENTICADO (admin dentro de empresa)
    public UsuarioDTO crear(UsuarioDTO dto) {
        validarAccesoEmpresa(dto.getEmpresaId());

        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }

        Usuario guardado = repo.save(toEntity(dto, true));
        return toDTO(guardado);
    }

    // Crear usuario SIN validar empresa del token (para /auth/register)
    public UsuarioDTO crearSinValidacion(UsuarioDTO dto) {
        if (dto.getEmpresaId() == null) {
            throw new IllegalArgumentException("empresaId es obligatorio");
        }

        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }

        Usuario guardado = repo.save(toEntity(dto, true));
        return toDTO(guardado);
    }

    public UsuarioDTO actualizar(Long id, UsuarioDTO dto) {
        Usuario u = repo.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        validarAccesoEmpresa(u.getEmpresa().getId());

        if (dto.getEmail() != null
                && !dto.getEmail().equalsIgnoreCase(u.getEmail())
                && repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }

        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId());
        u.setEmpresa(e);

        u.setNombres(dto.getNombres());
        u.setApellidos(dto.getApellidos());
        u.setEmail(dto.getEmail());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        return toDTO(repo.save(u));
    }

    public void eliminar(Long id) {
        Usuario u = repo.findById(id)
                        .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        validarAccesoEmpresa(u.getEmpresa().getId());
        repo.delete(u);
    }

    // ==================== VALIDACIÓN EMPRESA ====================

    private void validarAccesoEmpresa(Long empresaId) {
        Long currentCompanyId = securityUtils.currentCompanyId();
        if (currentCompanyId == null || !currentCompanyId.equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado a esta empresa");
        }
    }
}