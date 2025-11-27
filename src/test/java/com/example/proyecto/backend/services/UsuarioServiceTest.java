package com.example.proyecto.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.UsuarioDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Usuario;
import com.example.proyecto.backend.repository.UsuarioRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UsuarioDTO usuarioDTO;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombres("John");
        usuario.setApellidos("Doe");
        usuario.setEmail("john.doe@example.com");
        usuario.setPassword("encodedPassword");
        usuario.setEmpresa(empresa);

        usuarioDTO = new UsuarioDTO();
        usuarioDTO.setId(1L);
        usuarioDTO.setNombres("John");
        usuarioDTO.setApellidos("Doe");
        usuarioDTO.setEmail("john.doe@example.com");
        usuarioDTO.setPassword("password123");
        usuarioDTO.setEmpresaId(1L);
    }

    @Test
    void testObtenerTodosSuccess() {
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(usuarioRepository.findAllByEmpresa_Id(1L)).thenReturn(Collections.singletonList(usuario));

        List<UsuarioDTO> result = usuarioService.obtenerTodos();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(usuario.getEmail(), result.get(0).getEmail());
        verify(usuarioRepository).findAllByEmpresa_Id(1L);
    }

    @Test
    void testObtenerTodosNoCompany() {
        when(securityUtils.currentCompanyId()).thenReturn(null);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.obtenerTodos();
        });
        
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testCrearSuccess() {
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        UsuarioDTO result = usuarioService.crear(usuarioDTO);

        assertNotNull(result);
        assertEquals(usuarioDTO.getEmail(), result.getEmail());
        verify(passwordEncoder).encode("password123");
        verify(usuarioRepository).save(any(Usuario.class));
    }

    @Test
    void testCrearForbidden() {
        when(securityUtils.currentCompanyId()).thenReturn(2L); // Different company

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.crear(usuarioDTO);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void testCrearEmailExists() {
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crear(usuarioDTO);
        });
        
        assertEquals("Email ya existe", exception.getMessage());
    }

    @Test
    void testActualizarSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(inv -> inv.getArgument(0));
        
        UsuarioDTO updatedDto = new UsuarioDTO();
        updatedDto.setEmpresaId(1L);
        updatedDto.setNombres("Johnathan");
        updatedDto.setApellidos("Doer");
        updatedDto.setEmail("john.doe@example.com");
        updatedDto.setPassword("newPassword");


        UsuarioDTO result = usuarioService.actualizar(1L, updatedDto);

        assertNotNull(result);
        assertEquals(updatedDto.getNombres(), result.getNombres());
        verify(usuarioRepository).save(any(Usuario.class));
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void testActualizarNotFound() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> {
            usuarioService.actualizar(1L, usuarioDTO);
        });
    }

    @Test
    void testEliminarSuccess() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        doNothing().when(usuarioRepository).delete(any(Usuario.class));

        assertDoesNotThrow(() -> usuarioService.eliminar(1L));

        verify(usuarioRepository).delete(usuario);
    }

    @Test
    void testEliminarForbidden() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(securityUtils.currentCompanyId()).thenReturn(2L);

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            usuarioService.eliminar(1L);
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }
}
