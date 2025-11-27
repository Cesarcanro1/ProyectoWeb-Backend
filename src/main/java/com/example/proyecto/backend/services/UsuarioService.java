package com.example.proyecto.backend.services;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.UsuarioDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Role;
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

    // DTO MAPPING
    

    private UsuarioDTO toDTO(Usuario u) {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(u.getId());
        dto.setEmpresaId(u.getEmpresa().getId());
        dto.setNombres(u.getNombres());
        dto.setApellidos(u.getApellidos());
        dto.setEmail(u.getEmail());
        dto.setRole(u.getRole());
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

        // Rol solo si viene del DTO
        u.setRole(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            u.setPassword(
                encodePassword ? passwordEncoder.encode(dto.getPassword()) : dto.getPassword()
            );
        }

        return u;
    }

    
    // QUERIES
  

    public List<UsuarioDTO> obtenerTodos() {
        Long empresaId = validarEmpresaDelToken();
        return repo.findAllByEmpresa_Id(empresaId).stream()
                .map(this::toDTO)
                .toList();
    }

    public UsuarioDTO obtenerPorId(Long id) {
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));
        validarAccesoEmpresa(u.getEmpresa().getId());
        return toDTO(u);
    }

    
    // CREAR USUARIO
   

    public UsuarioDTO crear(UsuarioDTO dto) {
        Long empresaId = validarEmpresaDelToken();

        validarRolAdmin(); // solo ADMIN crea usuarios
        validarAccesoEmpresa(dto.getEmpresaId());

        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email ya existe");
        }

        Usuario nuevo = toEntity(dto, true);
        return toDTO(repo.save(nuevo));
    }

    // Crear usuario ADMIN inicial al crear empresa
    public UsuarioDTO crearAdminInicial(Long empresaId, String email, String nombres, String apellidos, String password) {

        if (repo.existsByEmail(email)) {
            throw new IllegalArgumentException("Correo ya registrado");
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setEmpresaId(empresaId);
        dto.setEmail(email);
        dto.setNombres(nombres);
        dto.setApellidos(apellidos);
        dto.setPassword(password);
        dto.setRole(Role.ADMIN);

        Usuario u = toEntity(dto, true);
        return toDTO(repo.save(u));
    }

    // ACTUALIZAR


    public UsuarioDTO actualizar(Long id, UsuarioDTO datos) {
        Usuario actual = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        validarAccesoEmpresa(actual.getEmpresa().getId());

        // Empresa no se puede cambiar
        if (!actual.getEmpresa().getId().equals(datos.getEmpresaId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cambiar la empresa del usuario");
        }

        // Rol solo lo modifica un ADMIN
        if (datos.getRole() != null && datos.getRole() != actual.getRole()) {
            validarRolAdmin();
            actual.setRole(datos.getRole());
        }

        actual.setNombres(datos.getNombres());
        actual.setApellidos(datos.getApellidos());

        // Email
        if (datos.getEmail() != null && !datos.getEmail().equalsIgnoreCase(actual.getEmail())) {
            if (repo.existsByEmail(datos.getEmail())) {
                throw new IllegalArgumentException("Email ya existe");
            }
            actual.setEmail(datos.getEmail());
        }

        // Password
        if (datos.getPassword() != null && !datos.getPassword().isBlank()) {
            actual.setPassword(passwordEncoder.encode(datos.getPassword()));
        }

        return toDTO(repo.save(actual));
    }

    // ELIMINAR
    

    public void eliminar(Long id) {
        validarRolAdmin();
        Usuario u = repo.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado"));

        validarAccesoEmpresa(u.getEmpresa().getId());
        repo.delete(u);
    }

   
    // VALIDACIONES
    

    private Long validarEmpresaDelToken() {
        Long empresaId = securityUtils.currentCompanyId();
        if (empresaId == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin empresa asociada");
        }
        return empresaId;
    }

    private void validarAccesoEmpresa(Long empresaId) {
        Long current = validarEmpresaDelToken();
        if (!current.equals(empresaId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado a esta empresa");
        }
    }

    private void validarRolAdmin() {
        String email = securityUtils.currentEmail();
        Usuario u = repo.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        if (u.getRole() != Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Solo ADMIN puede realizar esta acción");
        }
    }
}
