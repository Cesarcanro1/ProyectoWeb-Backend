package com.example.proyecto.backend.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.example.proyecto.backend.dtos.ArcoDTO;
import com.example.proyecto.backend.entity.Arco;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.ArcoRepository;
import com.example.proyecto.backend.repository.ProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class ArcoServiceTest {

    @Mock
    private ArcoRepository arcoRepository;

    @Mock
    private ProcesoRepository procesoRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private ArcoService arcoService;

    private Arco arco;
    private ArcoDTO arcoDTO;
    private Proceso proceso;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);

        proceso = new Proceso();
        proceso.setId(1L);
        proceso.setEmpresa(empresa);

        arco = new Arco();
        arco.setId(1L);
        arco.setProceso(proceso);

        arcoDTO = new ArcoDTO();
        arcoDTO.setId(1L);
        arcoDTO.setProcesoId(1L);
    }

    private void mockSecurity(Long companyId) {
        when(securityUtils.currentCompanyId()).thenReturn(companyId);
    }

    @Test
    void testObtenerPorProcesoSuccess() {
        mockSecurity(1L);
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(arcoRepository.findAllByProceso_Id(1L)).thenReturn(Collections.singletonList(arco));

        List<ArcoDTO> result = arcoService.obtenerPorProceso(1L);

        assertFalse(result.isEmpty());
        verify(arcoRepository).findAllByProceso_Id(1L);
    }

    @Test
    void testObtenerPorProcesoForbidden() {
        mockSecurity(2L); // Different company
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            arcoService.obtenerPorProceso(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testCrearSuccess() {
        mockSecurity(1L);
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(arcoRepository.save(any(Arco.class))).thenReturn(arco);

        ArcoDTO result = arcoService.crear(arcoDTO);

        assertNotNull(result);
        verify(arcoRepository).save(any(Arco.class));
    }

    @Test
    void testCrearForbidden() {
        mockSecurity(2L); // Different company
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            arcoService.crear(arcoDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testActualizarSuccess() {
        mockSecurity(1L);
        when(arcoRepository.findById(1L)).thenReturn(Optional.of(arco));
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(arcoRepository.save(any(Arco.class))).thenAnswer(inv -> inv.getArgument(0));

        ArcoDTO dtoToUpdate = new ArcoDTO();
        dtoToUpdate.setProcesoId(1L);
        dtoToUpdate.setCondicion("x > 5");

        ArcoDTO result = arcoService.actualizar(1L, dtoToUpdate);

        assertNotNull(result);
        assertEquals("x > 5", result.getCondicion());
    }

    @Test
    void testActualizarForbidden() {
        mockSecurity(2L); // Different company
        when(arcoRepository.findById(1L)).thenReturn(Optional.of(arco));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            arcoService.actualizar(1L, arcoDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
    
    @Test
    void testEliminarSuccess() {
        mockSecurity(1L);
        when(arcoRepository.findById(1L)).thenReturn(Optional.of(arco));
        doNothing().when(arcoRepository).delete(any(Arco.class));

        assertDoesNotThrow(() -> arcoService.eliminar(1L));

        verify(arcoRepository).delete(arco);
    }
    
    @Test
    void testEliminarForbidden() {
        mockSecurity(2L); // Different company
        when(arcoRepository.findById(1L)).thenReturn(Optional.of(arco));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            arcoService.eliminar(1L);
        });
        
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
