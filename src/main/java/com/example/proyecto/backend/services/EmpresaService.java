package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.EmpresaDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.repository.EmpresaRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository repo;
    private final SecurityUtils securityUtils;

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

    // Obtener solo la empresa del usuario autenticado
    public EmpresaDTO obtenerMiEmpresa() {
        Long companyId = securityUtils.currentCompanyId();
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada al usuario actual");
        }
        Empresa e = repo.findById(companyId)
                .orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        return toDTO(e);
    }

    // Bloquea listar todas (solo admins deberían hacerlo)
    public List<EmpresaDTO> obtenerTodos() {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado: solo administradores");
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

    public EmpresaDTO crear(EmpresaDTO dto) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Creación de empresas no permitida");
    }

    public EmpresaDTO actualizar(Long id, EmpresaDTO dto) {
        Long companyId = securityUtils.currentCompanyId();
        if (companyId == null || !companyId.equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes modificar otra empresa");
        }
        Empresa e = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Empresa no encontrada"));
        e.setNombre(dto.getNombre());
        e.setNit(dto.getNit());
        e.setCorreoContacto(dto.getCorreoContacto());
        return toDTO(repo.save(e));
    }

    public void eliminar(Long id) {
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Eliminación de empresas no permitida");
    }
}