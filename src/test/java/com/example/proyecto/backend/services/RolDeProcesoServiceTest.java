package com.example.proyecto.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.RolDeProcesoDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.RolDeProceso;
import com.example.proyecto.backend.repository.RolDeProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class RolDeProcesoServiceTest {

    @Mock
    private RolDeProcesoRepository rolRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private RolDeProcesoService rolService;

    private RolDeProceso rol;
    private RolDeProcesoDTO rolDTO;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);

        rol = new RolDeProceso();
        rol.setId(1L);
        rol.setNombre("Analista");
        rol.setEmpresa(empresa);

        rolDTO = new RolDeProcesoDTO();
        rolDTO.setId(1L);
        rolDTO.setEmpresaId(1L);
        rolDTO.setNombre("Analista DTO");
    }

    private void mockSecurity(Long companyId) {
        when(securityUtils.currentCompanyId()).thenReturn(companyId);
    }

    @Test
    void testObtenerTodosSuccess() {
        mockSecurity(1L);
        when(rolRepository.findAllByEmpresa_Id(1L)).thenReturn(Collections.singletonList(rol));

        List<RolDeProcesoDTO> result = rolService.obtenerTodos();

        assertFalse(result.isEmpty());
        verify(rolRepository).findAllByEmpresa_Id(1L);
    }
    
    @Test
    void testObtenerPorIdSuccess() {
        mockSecurity(1L);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        RolDeProcesoDTO result = rolService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(rol.getNombre(), result.getNombre());
    }

    @Test
    void testObtenerPorIdForbidden() {
        mockSecurity(2L); // Different company
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            rolService.obtenerPorId(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testCrearSuccess() {
        mockSecurity(1L);
        when(rolRepository.save(any(RolDeProceso.class))).thenReturn(rol);

        RolDeProcesoDTO result = rolService.crear(rolDTO);

        assertNotNull(result);
        verify(rolRepository).save(any(RolDeProceso.class));
    }

    @Test
    void testCrearForbidden() {
        mockSecurity(2L); // Different company

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            rolService.crear(rolDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testActualizarSuccess() {
        mockSecurity(1L);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(rolRepository.save(any(RolDeProceso.class))).thenAnswer(inv -> inv.getArgument(0));

        RolDeProcesoDTO dtoToUpdate = new RolDeProcesoDTO();
        dtoToUpdate.setEmpresaId(1L);
        dtoToUpdate.setNombre("Analista Senior");

        RolDeProcesoDTO result = rolService.actualizar(1L, dtoToUpdate);

        assertNotNull(result);
        assertEquals("Analista Senior", result.getNombre());
    }

    @Test
    void testActualizarForbidden() {
        mockSecurity(2L); // Different company
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            rolService.actualizar(1L, rolDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
    
    @Test
    void testEliminarSuccess() {
        mockSecurity(1L);
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        doNothing().when(rolRepository).delete(any(RolDeProceso.class));

        assertDoesNotThrow(() -> rolService.eliminar(1L));

        verify(rolRepository).delete(rol);
    }
    
    @Test
    void testEliminarForbidden() {
        mockSecurity(2L); // Different company
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            rolService.eliminar(1L);
        });
        
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
