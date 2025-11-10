package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.RolDeProcesoDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.RolDeProceso;
import com.example.proyecto.backend.repository.RolDeProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolDeProcesoService {

    private final RolDeProcesoRepository repo;
    private final SecurityUtils securityUtils;

    private RolDeProcesoDTO toDTO(RolDeProceso r) {
        RolDeProcesoDTO dto = new RolDeProcesoDTO();
        dto.setId(r.getId());
        dto.setEmpresaId(r.getEmpresa().getId());
        dto.setNombre(r.getNombre());
        dto.setDescripcion(r.getDescripcion());
        return dto;
    }

    private RolDeProceso toEntity(RolDeProcesoDTO dto) {
        RolDeProceso r = new RolDeProceso();
        r.setId(dto.getId());

        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId());
        r.setEmpresa(e);

        r.setNombre(dto.getNombre());
        r.setDescripcion(dto.getDescripcion());
        return r;
    }

    // Verifica que el usuario logueado pertenezca a la misma empresa
    private void validarAccesoEmpresa(Long empresaId) {
        Long currentCompanyId = securityUtils.currentCompanyId();
        if (currentCompanyId == null || !currentCompanyId.equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado a esta empresa");
        }
    }

    // CRUD
    public List<RolDeProcesoDTO> obtenerTodos() {
        Long currentCompanyId = securityUtils.currentCompanyId();
        if (currentCompanyId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada");
        }
        return repo.findAllByEmpresa_Id(currentCompanyId).stream().map(this::toDTO).toList();
    }

    public List<RolDeProcesoDTO> obtenerPorEmpresa(Long empresaId) {
        validarAccesoEmpresa(empresaId);
        return repo.findAllByEmpresa_Id(empresaId).stream().map(this::toDTO).toList();
    }

    public RolDeProcesoDTO obtenerPorId(Long id) {
        RolDeProceso r = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));
        validarAccesoEmpresa(r.getEmpresa().getId());
        return toDTO(r);
    }

    public RolDeProcesoDTO crear(RolDeProcesoDTO dto) {
        validarAccesoEmpresa(dto.getEmpresaId());
        RolDeProceso guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public RolDeProcesoDTO actualizar(Long id, RolDeProcesoDTO dto) {
        RolDeProceso r = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));
        validarAccesoEmpresa(r.getEmpresa().getId());

        Empresa e = new Empresa();
        e.setId(dto.getEmpresaId());
        r.setEmpresa(e);

        r.setNombre(dto.getNombre());
        r.setDescripcion(dto.getDescripcion());
        return toDTO(repo.save(r));
    }

    public void eliminar(Long id) {
        RolDeProceso r = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));
        validarAccesoEmpresa(r.getEmpresa().getId());
        repo.delete(r);
    }
}