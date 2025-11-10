package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.ProcesoDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProcesoService {

    private final ProcesoRepository repo;
    private final SecurityUtils securityUtils;

    // -------- mapping helpers --------
    private ProcesoDTO toDTO(Proceso p) {
        ProcesoDTO dto = new ProcesoDTO();
        dto.setId(p.getId());
        dto.setEmpresaId(p.getEmpresa().getId());
        dto.setNombre(p.getNombre());
        dto.setDescripcion(p.getDescripcion());
        dto.setCategoria(p.getCategoria());
        dto.setEstado(p.getEstado());
        return dto;
    }

    private Proceso toEntity(ProcesoDTO dto) {
        Proceso p = new Proceso();
        p.setId(dto.getId());

        Empresa e = new Empresa(); // map por id sin ir a BD
        e.setId(dto.getEmpresaId());
        p.setEmpresa(e);

        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setCategoria(dto.getCategoria());
        p.setEstado(dto.getEstado());
        return p;
    }

    // -------- guard de empresa --------
    private void validarAccesoEmpresa(Long empresaId) {
        Long currentCompanyId = securityUtils.currentCompanyId();
        if (currentCompanyId == null || !currentCompanyId.equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado a esta empresa");
        }
    }

    // -------- CRUD con scope por empresa --------
    public List<ProcesoDTO> obtenerTodos() {
        Long currentCompanyId = securityUtils.currentCompanyId();
        if (currentCompanyId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada");
        }
        return repo.findAllByEmpresa_Id(currentCompanyId).stream().map(this::toDTO).toList();
    }

    public List<ProcesoDTO> obtenerPorEmpresa(Long empresaId) {
        validarAccesoEmpresa(empresaId);
        return repo.findAllByEmpresa_Id(empresaId).stream().map(this::toDTO).toList();
    }

    public ProcesoDTO obtenerPorId(Long id) {
        Proceso p = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));
        validarAccesoEmpresa(p.getEmpresa().getId());
        return toDTO(p);
    }

    public ProcesoDTO crear(ProcesoDTO dto) {
        validarAccesoEmpresa(dto.getEmpresaId());
        Proceso guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public ProcesoDTO actualizar(Long id, ProcesoDTO dto) {
        // Primero verificamos contra la empresa del registro actual para evitar escalamiento
        Proceso p = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));
        validarAccesoEmpresa(p.getEmpresa().getId());

        // Ahora sí, actualizamos campos (incluyendo empresa)
        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId());
        p.setEmpresa(e);

        p.setNombre(dto.getNombre());
        p.setDescripcion(dto.getDescripcion());
        p.setCategoria(dto.getCategoria());
        p.setEstado(dto.getEstado());

        return toDTO(repo.save(p));
    }

    public void eliminar(Long id) {
        Proceso p = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));
        validarAccesoEmpresa(p.getEmpresa().getId());
        repo.delete(p); 
    }
}
