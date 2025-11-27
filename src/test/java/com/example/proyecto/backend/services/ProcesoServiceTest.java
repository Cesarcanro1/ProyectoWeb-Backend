package com.example.proyecto.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import com.example.proyecto.backend.dtos.ProcesoDTO;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class ProcesoServiceTest {

    @Mock
    private ProcesoRepository procesoRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ProcesoService procesoService;

    private Proceso proceso;
    private ProcesoDTO procesoDTO;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);

        proceso = new Proceso();
        proceso.setId(1L);
        proceso.setNombre("Proceso de Prueba");
        proceso.setDescripcion("Descripción de prueba");
        proceso.setEmpresa(empresa);

        procesoDTO = new ProcesoDTO();
        procesoDTO.setId(1L);
        procesoDTO.setNombre("Proceso DTO");
        procesoDTO.setDescripcion("Descripción DTO");
        procesoDTO.setEmpresaId(1L);
    }

    @Test
    void testObtenerTodosSuccess() {
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(procesoRepository.findAllByEmpresa_Id(1L)).thenReturn(Collections.singletonList(proceso));

        List<ProcesoDTO> result = procesoService.obtenerTodos();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(procesoRepository).findAllByEmpresa_Id(1L);
    }

    @Test
    void testObtenerPorIdSuccess() {
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(securityUtils.currentCompanyId()).thenReturn(1L);

        ProcesoDTO result = procesoService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals(proceso.getNombre(), result.getNombre());
    }

    @Test
    void testObtenerPorIdNotFound() {
        when(procesoRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> procesoService.obtenerPorId(1L));
    }

    @Test
    void testObtenerPorIdForbidden() {
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(securityUtils.currentCompanyId()).thenReturn(2L); // Different company

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            procesoService.obtenerPorId(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
    
    @Test
    void testCrearSuccess() {
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(procesoRepository.save(any(Proceso.class))).thenAnswer(inv -> inv.getArgument(0));

        ProcesoDTO result = procesoService.crear(procesoDTO);

        assertNotNull(result);
        assertEquals(procesoDTO.getNombre(), result.getNombre());
        verify(procesoRepository).save(any(Proceso.class));
    }

    @Test
    void testCrearForbidden() {
        when(securityUtils.currentCompanyId()).thenReturn(2L); // Different company

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            procesoService.crear(procesoDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testActualizarSuccess() {
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        when(procesoRepository.save(any(Proceso.class))).thenAnswer(inv -> inv.getArgument(0));

        ProcesoDTO dtoToUpdate = new ProcesoDTO();
        dtoToUpdate.setEmpresaId(1L);
        dtoToUpdate.setNombre("Nombre Actualizado");

        ProcesoDTO result = procesoService.actualizar(1L, dtoToUpdate);

        assertNotNull(result);
        assertEquals("Nombre Actualizado", result.getNombre());
        verify(procesoRepository).save(any(Proceso.class));
    }

    @Test
    void testActualizarForbidden() {
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(securityUtils.currentCompanyId()).thenReturn(2L); // Different company

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            procesoService.actualizar(1L, procesoDTO);
        });

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(procesoRepository, never()).save(any(Proceso.class));
    }

    @Test
    void testEliminarSuccess() {
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(securityUtils.currentCompanyId()).thenReturn(1L);
        doNothing().when(procesoRepository).delete(any(Proceso.class));

        assertDoesNotThrow(() -> procesoService.eliminar(1L));

        verify(procesoRepository).delete(proceso);
    }

    @Test
    void testEliminarForbidden() {
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(securityUtils.currentCompanyId()).thenReturn(2L); // Different company

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            procesoService.eliminar(1L);
        });
        
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        verify(procesoRepository, never()).delete(any(Proceso.class));
    }
}
