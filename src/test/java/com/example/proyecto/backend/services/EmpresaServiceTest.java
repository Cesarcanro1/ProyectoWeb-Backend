package com.example.proyecto.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.EmpresaDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.repository.EmpresaRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresa;
    private EmpresaDTO empresaDTO;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNombre("Test Empresa");
        empresa.setNit("123456789");
        empresa.setCorreoContacto("test@empresa.com");

        empresaDTO = new EmpresaDTO();
        empresaDTO.setId(1L);
        empresaDTO.setNombre("Updated Empresa");
        empresaDTO.setNit("987654321");
        empresaDTO.setCorreoContacto("updated@empresa.com");
    }

    @Test
    void testActualizarSuccess() {
        // Arrange
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaRepository.save(any(Empresa.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmpresaDTO result = empresaService.actualizar(1L, empresaDTO);

        // Assert
        assertNotNull(result);
        assertEquals(empresaDTO.getNombre(), result.getNombre());
        assertEquals(empresaDTO.getNit(), result.getNit());
        assertEquals(empresaDTO.getCorreoContacto(), result.getCorreoContacto());

        verify(securityUtils).currentCompanyId();
        verify(empresaRepository).findById(1L);
        verify(empresaRepository).save(any(Empresa.class));
    }

    @Test
    void testActualizarForbidden() {
        // Arrange
        when(securityUtils.currentCompanyId()).thenReturn(2L); // Different company ID

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            empresaService.actualizar(1L, empresaDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());

        verify(securityUtils).currentCompanyId();
        verify(empresaRepository, never()).findById(anyLong());
        verify(empresaRepository, never()).save(any(Empresa.class));
    }

    @Test
    void testObtenerMiEmpresaSuccess() {
        // Arrange
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        // Act
        EmpresaDTO result = empresaService.obtenerMiEmpresa();

        // Assert
        assertNotNull(result);
        assertEquals(empresa.getNombre(), result.getNombre());
        verify(securityUtils).currentCompanyId();
        verify(empresaRepository).findById(1L);
    }

    @Test
    void testObtenerMiEmpresaNoCompany() {
        // Arrange
        when(securityUtils.currentCompanyId()).thenReturn(null);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            empresaService.obtenerMiEmpresa();
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(securityUtils).currentCompanyId();
        verify(empresaRepository, never()).findById(anyLong());
    }
    
    @Test
    void testObtenerMiEmpresaNotFound() {
        // Arrange
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NoSuchElementException.class, () -> {
            empresaService.obtenerMiEmpresa();
        });
    }

    @Test
    void testObtenerPorIdSuccess() {
        // Arrange
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        // Act
        EmpresaDTO result = empresaService.obtenerPorId(1L);

        // Assert
        assertNotNull(result);
        assertEquals(empresa.getNombre(), result.getNombre());
        verify(securityUtils).currentCompanyId();
        verify(empresaRepository).findById(1L);
    }

    @Test
    void testObtenerPorIdForbidden() {
        // Arrange
        when(securityUtils.currentCompanyId()).thenReturn(2L); // Different company ID

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            empresaService.obtenerPorId(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verify(securityUtils).currentCompanyId();
        verify(empresaRepository, never()).findById(anyLong());
    }

    @Test
    void testForbiddenMethods() {
        // Test obtenerTodos
        ResponseStatusException ex1 = assertThrows(ResponseStatusException.class, () -> empresaService.obtenerTodos());
        assertEquals(HttpStatus.FORBIDDEN, ex1.getStatusCode());

        // Test crear
        ResponseStatusException ex2 = assertThrows(ResponseStatusException.class, () -> empresaService.crear(empresaDTO));
        assertEquals(HttpStatus.FORBIDDEN, ex2.getStatusCode());

        // Test eliminar
        ResponseStatusException ex3 = assertThrows(ResponseStatusException.class, () -> empresaService.eliminar(1L));
        assertEquals(HttpStatus.FORBIDDEN, ex3.getStatusCode());
    }
}
