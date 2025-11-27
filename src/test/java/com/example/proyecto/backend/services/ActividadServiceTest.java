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

import com.example.proyecto.backend.dtos.ActividadDTO;
import com.example.proyecto.backend.entity.Actividad;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ActividadRepository;
import com.example.proyecto.backend.repository.ProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class ActividadServiceTest {

    @Mock
    private ActividadRepository actividadRepository;

    @Mock
    private ProcesoRepository procesoRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ActividadService actividadService;

    private Actividad actividad;
    private ActividadDTO actividadDTO;
    private Proceso proceso;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);

        Proceso otroProceso = new Proceso();
        otroProceso.setId(2L);
        Empresa otraEmpresa = new Empresa();
        otraEmpresa.setId(2L);
        otroProceso.setEmpresa(otraEmpresa);

        proceso = new Proceso();
        proceso.setId(1L);
        proceso.setEmpresa(empresa);

        actividad = new Actividad();
        actividad.setId(1L);
        actividad.setNombre("Actividad de Prueba");
        actividad.setProceso(proceso);

        actividadDTO = new ActividadDTO();
        actividadDTO.setId(1L);
        actividadDTO.setNombre("Actividad DTO");
        actividadDTO.setProcesoId(1L);
    }

    private void mockSecurity(Long companyId) {
        when(securityUtils.currentCompanyId()).thenReturn(companyId);
    }

    @Test
    void testObtenerPorProcesoSuccess() {
        mockSecurity(1L);
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(actividadRepository.findAllByProceso_Id(1L)).thenReturn(Collections.singletonList(actividad));

        List<ActividadDTO> result = actividadService.obtenerPorProceso(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(actividadRepository).findAllByProceso_Id(1L);
    }

    @Test
    void testObtenerPorProcesoForbidden() {
        mockSecurity(2L); // User from a different company
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            actividadService.obtenerPorProceso(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testCrearSuccess() {
        mockSecurity(1L);
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

        ActividadDTO result = actividadService.crear(actividadDTO);

        assertNotNull(result);
        assertEquals(actividadDTO.getNombre(), result.getNombre());
        verify(actividadRepository).save(any(Actividad.class));
    }

    @Test
    void testCrearInProcessFromOtherCompany() {
        mockSecurity(2L); // User from a different company
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            actividadService.crear(actividadDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testActualizarSuccess() {
        mockSecurity(1L);
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));
        // For validation of the new process
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(actividadRepository.save(any(Actividad.class))).thenAnswer(inv -> inv.getArgument(0));

        ActividadDTO dtoToUpdate = new ActividadDTO();
        dtoToUpdate.setProcesoId(1L);
        dtoToUpdate.setNombre("Nombre Actualizado");

        ActividadDTO result = actividadService.actualizar(1L, dtoToUpdate);

        assertNotNull(result);
        assertEquals("Nombre Actualizado", result.getNombre());
    }

    @Test
    void testActualizarForbidden() {
        // User from company 2 tries to update activity from company 1
        mockSecurity(2L);
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            actividadService.actualizar(1L, actividadDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
    
    @Test
    void testEliminarSuccess() {
        mockSecurity(1L);
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));
        doNothing().when(actividadRepository).delete(any(Actividad.class));

        assertDoesNotThrow(() -> actividadService.eliminar(1L));

        verify(actividadRepository).delete(actividad);
    }
    
    @Test
    void testEliminarForbidden() {
        mockSecurity(2L);
        when(actividadRepository.findById(1L)).thenReturn(Optional.of(actividad));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            actividadService.eliminar(1L);
        });
        
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
