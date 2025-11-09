package com.example.proyecto.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyecto.backend.entity.RolDeProceso;

@Repository
public interface RolDeProcesoRepository extends JpaRepository<RolDeProceso, Long> {
    List<RolDeProceso> findAllByEmpresa_Id(Long empresaId);
}
