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

    // -------- helpers --------
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

    // -------- validaciones --------
    private Long currentCompanyIdOr403() {
        Long cid = securityUtils.currentCompanyId();
        if (cid == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada");
        return cid;
    }

    private void validarAccesoProceso(Long procesoId) {
        Long currentCid = currentCompanyIdOr403();
        Long ownerCid = procesoRepo.findById(procesoId)
                .map(p -> p.getEmpresa() != null ? p.getEmpresa().getId() : null)
                .orElseThrow(() -> new NoSuchElementException("Proceso no encontrado"));
        if (!currentCid.equals(ownerCid)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado al proceso");
        }
    }

    private void validarAccesoArco(Arco a) {
        Long currentCid = currentCompanyIdOr403();
        Long ownerCid = a.getProceso().getEmpresa() != null ? a.getProceso().getEmpresa().getId() : null;
        if (!currentCid.equals(ownerCid)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado al arco");
        }
    }

    // -------- CRUD con scoping --------
    public List<ArcoDTO> obtenerTodos() {
        Long currentCid = currentCompanyIdOr403();
        return repo.findAllByProceso_Empresa_Id(currentCid).stream().map(this::toDTO).toList();
    }

    public List<ArcoDTO> obtenerPorProceso(Long procesoId) {
        validarAccesoProceso(procesoId);
        return repo.findAllByProceso_Id(procesoId).stream().map(this::toDTO).toList();
    }

    public ArcoDTO obtenerPorId(Long id) {
        Arco a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));
        validarAccesoArco(a);
        return toDTO(a);
    }

    public ArcoDTO crear(ArcoDTO dto) {
        validarAccesoProceso(dto.getProcesoId());
        Arco guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public ArcoDTO actualizar(Long id, ArcoDTO dto) {
        Arco a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));
        validarAccesoArco(a);                // valida empresa del arco actual
        validarAccesoProceso(dto.getProcesoId()); // valida empresa del nuevo proceso (si cambia)

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
        Arco a = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Arco no encontrado"));
        validarAccesoArco(a);
        repo.delete(a);
    }
}
