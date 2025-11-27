package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.RolDeProcesoDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.RolDeProceso;
import com.example.proyecto.backend.entity.Role;
import com.example.proyecto.backend.repository.RolDeProcesoRepository;
import com.example.proyecto.backend.repository.UsuarioRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolDeProcesoService {

    private final RolDeProcesoRepository repo;
    private final UsuarioRepository usuarioRepo;
    private final SecurityUtils securityUtils;

    // ---------------- Helpers ----------------

    private RolDeProcesoDTO toDTO(RolDeProceso r) {
        RolDeProcesoDTO dto = new RolDeProcesoDTO();
        dto.setId(r.getId());
        dto.setEmpresaId(r.getEmpresa().getId());
        dto.setNombre(r.getNombre());
        dto.setDescripcion(r.getDescripcion());
        return dto;
    }

    private Long empresaActual() {
        Long cid = securityUtils.currentCompanyId();
        if (cid == null)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin empresa asociada");
        return cid;
    }

    private void validarEmpresa(Long empresaId) {
        if (!empresaActual().equals(empresaId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado a esta empresa");
    }

    private Role rolUsuarioActual() {
        String email = securityUtils.currentEmail();
        return usuarioRepo.findByEmail(email)
                .map(u -> u.getRole())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
    }

    private void requireEditorOrAdmin() {
        Role r = rolUsuarioActual();
        if (r != Role.ADMIN && r != Role.EDITOR)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permisos para modificar roles");
    }

    private void requireAdmin() {
        if (rolUsuarioActual() != Role.ADMIN)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo ADMIN puede realizar esta acción");
    }

    // ---------------- CRUD ----------------

    public List<RolDeProcesoDTO> obtenerTodos() {
        Long empresaId = empresaActual();
        return repo.findAllByEmpresa_Id(empresaId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public List<RolDeProcesoDTO> obtenerPorEmpresa(Long empresaId) {
        validarEmpresa(empresaId);
        return repo.findAllByEmpresa_Id(empresaId).stream().map(this::toDTO).toList();
    }

    public RolDeProcesoDTO obtenerPorId(Long id) {
        RolDeProceso r = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));
        validarEmpresa(r.getEmpresa().getId());
        return toDTO(r);
    }

    public RolDeProcesoDTO crear(RolDeProcesoDTO dto) {
        requireEditorOrAdmin();
        Long empresaId = empresaActual();

        RolDeProceso r = new RolDeProceso();
        r.setEmpresa(new Empresa(empresaId, null, null, null, 0, null, null, null));
        r.setNombre(dto.getNombre());
        r.setDescripcion(dto.getDescripcion());

        return toDTO(repo.save(r));
    }

    public RolDeProcesoDTO actualizar(Long id, RolDeProcesoDTO dto) {
        requireEditorOrAdmin();

        RolDeProceso r = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));

        validarEmpresa(r.getEmpresa().getId());

        r.setNombre(dto.getNombre());
        r.setDescripcion(dto.getDescripcion());
        return toDTO(repo.save(r));
    }

    public void eliminar(Long id) {
        requireAdmin(); // solo admin elimina

        RolDeProceso r = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Rol no encontrado"));

        validarEmpresa(r.getEmpresa().getId());
        repo.delete(r);
    }
}
