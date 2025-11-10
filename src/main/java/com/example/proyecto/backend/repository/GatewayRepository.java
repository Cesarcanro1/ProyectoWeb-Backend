package com.example.proyecto.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyecto.backend.entity.Gateway;

@Repository
public interface GatewayRepository extends JpaRepository<Gateway, Long> {
    List<Gateway> findAllByProceso_Id(Long procesoId);
    List<Gateway> findAllByProceso_Empresa_Id(Long empresaId);
}
