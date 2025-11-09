package com.example.proyecto.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyecto.backend.entity.Proceso;

@Repository
public interface ProcesoRepository extends JpaRepository<Proceso, Long> {
    List<Proceso> findAllByEmpresa_Id(Long empresaId);
}