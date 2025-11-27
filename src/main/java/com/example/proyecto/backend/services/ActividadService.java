package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.ActividadDTO;
import com.example.proyecto.backend.entity.Actividad;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ActividadRepository;
import com.example.proyecto.backend.repository.ProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActividadService {

    private final ActividadRepository repo;
    private final ProcesoRepository procesoRepo;
    private final SecurityUtils securityUtils;

    // ========== MAPPERS ==========
    private ActividadDTO toDTO(Actividad a) {
        ActividadDTO dto = new ActividadDTO();
        dto.setId(a.getId());
        dto.setProcesoId(a.getProceso().getId());
        dto.setNombre(a.getNombre());
        dto.setTipo(a.getTipo());
        dto.setDescripcion(a.getDescripcion());
        dto.setRolResponsable(a.getRolResponsable());
        return dto;
    }

    private Actividad toEntity(ActividadDTO dto) {
        Actividad a = new Actividad();
        a.setId(dto.getId());

        Proceso p = new Proceso();
        p.setId(dto.getProcesoId());
        a.setProceso(p);

        a.setNombre(dto.getNombre());
        a.setTipo(dto.getTipo());
        a.setDescripcion(dto.getDescripcion());
        a.setRolResponsable(dto.getRolResponsable());

        return a;
    }

    // ========== VALIDACIÓN ==========
    private void validarAccesoProceso(Long procesoId) {
        Long current = securityUtils.currentCompanyId();
        if (current == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada");

        Long owner = procesoRepo.findById(procesoId)
                .map(p -> p.getEmpresa().getId())
                .orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));

        if (!owner.equals(current))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado al proceso");
    }

    private void validarAccesoActividad(Actividad a) {
        Long current = securityUtils.currentCompanyId();
        Long owner = a.getProceso().getEmpresa().getId();
        if (!current.equals(owner))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
    }

    // ========== CRUD ==========
    public List<ActividadDTO> obtenerTodos() {
        Long empresaId = securityUtils.currentCompanyId();
        if (empresaId == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada");

        return repo.findAllByProceso_Empresa_Id(empresaId).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ActividadDTO> obtenerPorProceso(Long procesoId) {
        validarAccesoProceso(procesoId);
        return repo.findAllByProceso_Id(procesoId).stream()
                .map(this::toDTO)
                .toList();
    }

    public ActividadDTO obtenerPorId(Long id) {
        Actividad a = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));
        validarAccesoActividad(a);
        return toDTO(a);
    }

    public ActividadDTO crear(ActividadDTO dto) {
        validarAccesoProceso(dto.getProcesoId());
        Actividad guardada = repo.save(toEntity(dto));
        return toDTO(guardada);
    }

    public ActividadDTO actualizar(Long id, ActividadDTO dto) {
        Actividad a = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));
        validarAccesoActividad(a);
        validarAccesoProceso(dto.getProcesoId());

        Proceso p = new Proceso();
        p.setId(dto.getProcesoId());
        a.setProceso(p);

        a.setNombre(dto.getNombre());
        a.setTipo(dto.getTipo());
        a.setDescripcion(dto.getDescripcion());
        a.setRolResponsable(dto.getRolResponsable());

        return toDTO(repo.save(a));
    }

    public void eliminar(Long id) {
        Actividad a = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Actividad no encontrada"));
        validarAccesoActividad(a);
        repo.delete(a);
    }
}
