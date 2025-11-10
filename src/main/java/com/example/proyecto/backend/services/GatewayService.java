package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.GatewayDTO;
import com.example.proyecto.backend.entity.Gateway;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.GatewayRepository;
import com.example.proyecto.backend.repository.ProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GatewayService {

    private final GatewayRepository repo;
    private final ProcesoRepository procesoRepo;
    private final SecurityUtils securityUtils;

    // -------- mapping helpers --------
    private GatewayDTO toDTO(Gateway g) {
        GatewayDTO dto = new GatewayDTO();
        dto.setId(g.getId());
        dto.setProcesoId(g.getProceso().getId());
        dto.setNombre(g.getNombre());
        dto.setTipo(g.getTipo());
        dto.setDescripcion(g.getDescripcion());
        return dto;
    }

    private Gateway toEntity(GatewayDTO dto) {
        Gateway g = new Gateway();
        g.setId(dto.getId());
        Proceso p = new Proceso(); // map sin ir a BD (ya validamos proceso antes)
        p.setId(dto.getProcesoId());
        g.setProceso(p);
        g.setNombre(dto.getNombre());
        g.setTipo(dto.getTipo());
        g.setDescripcion(dto.getDescripcion());
        return g;
    }

    // -------- guards de seguridad --------
    private Long currentCompanyIdOr403() {
        Long cid = securityUtils.currentCompanyId();
        if (cid == null) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No hay empresa asociada");
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

    private void validarAccesoGateway(Gateway g) {
        Long currentCid = currentCompanyIdOr403();
        Long ownerCid = g.getProceso().getEmpresa() != null ? g.getProceso().getEmpresa().getId() : null;
        if (!currentCid.equals(ownerCid)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado al gateway");
        }
    }

    // -------- CRUD con scope por empresa --------
    public List<GatewayDTO> obtenerTodos() {
        Long currentCid = currentCompanyIdOr403();
        return repo.findAllByProceso_Empresa_Id(currentCid).stream().map(this::toDTO).toList();
    }

    public List<GatewayDTO> obtenerPorProceso(Long procesoId) {
        validarAccesoProceso(procesoId);
        return repo.findAllByProceso_Id(procesoId).stream().map(this::toDTO).toList();
    }

    public GatewayDTO obtenerPorId(Long id) {
        Gateway g = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Gateway no encontrado"));
        validarAccesoGateway(g);
        return toDTO(g);
    }

    public GatewayDTO crear(GatewayDTO dto) {
        // El gateway siempre pertenece al proceso → valida el proceso
        validarAccesoProceso(dto.getProcesoId());
        Gateway guardado = repo.save(toEntity(dto));
        return toDTO(guardado);
    }

    public GatewayDTO actualizar(Long id, GatewayDTO dto) {
        Gateway g = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Gateway no encontrado"));
        // Valida empresa del gateway actual (evita escalamiento)
        validarAccesoGateway(g);
        // Si cambian de proceso, valida que el nuevo proceso sea de la misma empresa
        validarAccesoProceso(dto.getProcesoId());

        Proceso p = new Proceso();
        p.setId(dto.getProcesoId());
        g.setProceso(p);

        g.setNombre(dto.getNombre());
        g.setTipo(dto.getTipo());
        g.setDescripcion(dto.getDescripcion());
        return toDTO(repo.save(g));
    }

    public void eliminar(Long id) {
        Gateway g = repo.findById(id).orElseThrow(() -> new NoSuchElementException("Gateway no encontrado"));
        validarAccesoGateway(g);
        repo.delete(g); 
    }
}
