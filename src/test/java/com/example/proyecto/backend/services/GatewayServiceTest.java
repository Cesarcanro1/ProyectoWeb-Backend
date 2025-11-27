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

import com.example.proyecto.backend.dtos.GatewayDTO;
import com.example.proyecto.backend.entity.Gateway;
import com.example.proyecto.backend.entity.Empresa;
import com.example.proyecto.backend.entity.Proceso;
import com.example.proyecto.backend.repository.GatewayRepository;
import com.example.proyecto.backend.repository.ProcesoRepository;
import com.example.proyecto.backend.security.jwt.SecurityUtils;

@ExtendWith(MockitoExtension.class)
public class GatewayServiceTest {

    @Mock
    private GatewayRepository gatewayRepository;

    @Mock
    private ProcesoRepository procesoRepository;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private GatewayService gatewayService;

    private Gateway gateway;
    private GatewayDTO gatewayDTO;
    private Proceso proceso;
    private Empresa empresa;

    @BeforeEach
    void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);

        proceso = new Proceso();
        proceso.setId(1L);
        proceso.setEmpresa(empresa);

        gateway = new Gateway();
        gateway.setId(1L);
        gateway.setNombre("Gateway de Prueba");
        gateway.setProceso(proceso);

        gatewayDTO = new GatewayDTO();
        gatewayDTO.setId(1L);
        gatewayDTO.setProcesoId(1L);
        gatewayDTO.setNombre("Gateway DTO");
    }

    private void mockSecurity(Long companyId) {
        when(securityUtils.currentCompanyId()).thenReturn(companyId);
    }

    @Test
    void testObtenerPorProcesoSuccess() {
        mockSecurity(1L);
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(gatewayRepository.findAllByProceso_Id(1L)).thenReturn(Collections.singletonList(gateway));

        List<GatewayDTO> result = gatewayService.obtenerPorProceso(1L);

        assertFalse(result.isEmpty());
        verify(gatewayRepository).findAllByProceso_Id(1L);
    }

    @Test
    void testObtenerPorProcesoForbidden() {
        mockSecurity(2L); // Different company
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            gatewayService.obtenerPorProceso(1L);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testCrearSuccess() {
        mockSecurity(1L);
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(gatewayRepository.save(any(Gateway.class))).thenReturn(gateway);

        GatewayDTO result = gatewayService.crear(gatewayDTO);

        assertNotNull(result);
        verify(gatewayRepository).save(any(Gateway.class));
    }

    @Test
    void testCrearForbidden() {
        mockSecurity(2L); // Different company
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            gatewayService.crear(gatewayDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void testActualizarSuccess() {
        mockSecurity(1L);
        when(gatewayRepository.findById(1L)).thenReturn(Optional.of(gateway));
        when(procesoRepository.findById(1L)).thenReturn(Optional.of(proceso));
        when(gatewayRepository.save(any(Gateway.class))).thenAnswer(inv -> inv.getArgument(0));

        GatewayDTO dtoToUpdate = new GatewayDTO();
        dtoToUpdate.setProcesoId(1L);
        dtoToUpdate.setNombre("Nombre Actualizado");

        GatewayDTO result = gatewayService.actualizar(1L, dtoToUpdate);

        assertNotNull(result);
        assertEquals("Nombre Actualizado", result.getNombre());
    }

    @Test
    void testActualizarForbidden() {
        mockSecurity(2L); // Different company
        when(gatewayRepository.findById(1L)).thenReturn(Optional.of(gateway));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            gatewayService.actualizar(1L, gatewayDTO);
        });
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
    
    @Test
    void testEliminarSuccess() {
        mockSecurity(1L);
        when(gatewayRepository.findById(1L)).thenReturn(Optional.of(gateway));
        doNothing().when(gatewayRepository).delete(any(Gateway.class));

        assertDoesNotThrow(() -> gatewayService.eliminar(1L));

        verify(gatewayRepository).delete(gateway);
    }
    
    @Test
    void testEliminarForbidden() {
        mockSecurity(2L); // Different company
        when(gatewayRepository.findById(1L)).thenReturn(Optional.of(gateway));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            gatewayService.eliminar(1L);
        });
        
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }
}
