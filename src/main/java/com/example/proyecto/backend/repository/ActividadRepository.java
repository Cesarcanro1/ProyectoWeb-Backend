package com.example.proyecto.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyecto.backend.entity.Actividad;

@Repository
public interface ActividadRepository extends JpaRepository<Actividad, Long> {
    List<Actividad> findAllByProceso_Id(Long procesoId);
    List<Actividad> findAllByProceso_Empresa_Id(Long empresaId);
}
