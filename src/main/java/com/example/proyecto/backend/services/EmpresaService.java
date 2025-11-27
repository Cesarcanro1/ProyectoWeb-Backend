package com.example.proyecto.backend.services;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.EmpresaDTO;
import com.example.proyecto.backend.dtos.UsuarioDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Role;
import com.example.proyecto.backend.repository.EmpresaRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repo;
    private final SecurityUtils securityUtils;
    private final UsuarioService usuarioService;

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

    // =======================================
    // REGISTRO DE EMPRESA (HU-01)
    // =======================================

    public EmpresaDTO registrarEmpresa(EmpresaDTO dto) {

        if (repo.findByNit(dto.getNit()).isPresent()) {
            throw new IllegalArgumentException("El NIT ya está registrado");
        }

        Empresa nueva = repo.save(toEntity(dto));

        UsuarioDTO admin = dto.getAdmin();
        admin.setEmpresaId(nueva.getId());
        admin.setRole(Role.ADMIN);

        usuarioService.crearAdminInicial(
                nueva.getId(),
                admin.getEmail(),
                admin.getNombres(),
                admin.getApellidos(),
                admin.getPassword()
        );

        return toDTO(nueva);
    }

    // =======================================

    public EmpresaDTO obtenerMiEmpresa() {
        Long companyId = securityUtils.currentCompanyId();
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        Empresa e = repo.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        return toDTO(e);
    }

    public EmpresaDTO obtenerPorId(Long id) {
        Long companyId = securityUtils.currentCompanyId();
        if (companyId == null || !companyId.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a otra empresa");
        }
        Empresa e = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        return toDTO(e);
    }

    public EmpresaDTO actualizar(Long id, EmpresaDTO dto) {
        Long companyId = securityUtils.currentCompanyId();
        if (!id.equals(companyId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar otra empresa");
        }
        Empresa e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        e.setNombre(dto.getNombre());
        e.setNit(dto.getNit());
        e.setCorreoContacto(dto.getCorreoContacto());
        return toDTO(repo.save(e));
    }
}
