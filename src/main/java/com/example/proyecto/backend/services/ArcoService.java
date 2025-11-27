package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.ArcoDTO;
import com.example.proyecto.backend.entity.Arco;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ArcoRepository;
import com.example.proyecto.backend.repository.ProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ArcoService {

    private final ArcoRepository repo;
    private final ProcesoRepository procesoRepo;
    private final SecurityUtils securityUtils;

    // ========== DTO MAPPER ==========
    private ArcoDTO toDTO(Arco a) {
        ArcoDTO dto = new ArcoDTO();
        dto.setId(a.getId());
        dto.setProcesoId(a.getProceso().getId());
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

    // ========== VALIDACIÓN ==========
    private void validarAccesoProceso(Long procesoId) {
        Long current = securityUtils.currentCompanyId();
        if (current == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada");

        Long owner = procesoRepo.findById(procesoId)
                .map(p -> p.getEmpresa().getId())
                .orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));

        if (!owner.equals(current))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes acceder a este proceso");
    }

    private void validarAccesoArco(Arco a) {
        Long current = securityUtils.currentCompanyId();
        Long owner = a.getProceso().getEmpresa().getId();

        if (!owner.equals(current))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
    }

    // ========== CRUD ==========
    public List<ArcoDTO> obtenerTodos() {
        Long empresaId = securityUtils.currentCompanyId();
        if (empresaId == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada");

        return repo.findAllByProceso_Empresa_Id(empresaId).stream()
                .map(this::toDTO)
                .toList();
    }

    public List<ArcoDTO> obtenerPorProceso(Long procesoId) {
        validarAccesoProceso(procesoId);
        return repo.findAllByProceso_Id(procesoId).stream()
                .map(this::toDTO)
                .toList();
    }

    public ArcoDTO obtenerPorId(Long id) {
        Arco a = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));
        validarAccesoArco(a);
        return toDTO(a);
    }

    public ArcoDTO crear(ArcoDTO dto) {
        validarAccesoProceso(dto.getProcesoId());
        Arco guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public ArcoDTO actualizar(Long id, ArcoDTO dto) {
        Arco a = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));

        validarAccesoArco(a);
        validarAccesoProceso(dto.getProcesoId());

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

    public void eliminar(Long id) {
        Arco a = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));
        validarAccesoArco(a);
        repo.delete(a);
    }
}
