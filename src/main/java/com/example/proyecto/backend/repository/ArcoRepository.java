package com.example.proyecto.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.proyecto.backend.entity.Arco;

@Repository
public interface ArcoRepository extends JpaRepository<Arco, Long> {
    List<Arco> findAllByProceso_Id(Long procesoId);
    List<Arco> findAllByOrigenTipoAndOrigenId(String origenTipo, Long origenId);
    List<Arco> findAllByDestinoTipoAndDestinoId(String destinoTipo, Long destinoId);
}
