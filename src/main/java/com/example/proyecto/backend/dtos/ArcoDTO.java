package com.example.proyecto.backend.dtos;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ArcoDTO {
    private Long id;
    private Long procesoId;
    private String origenTipo;  // "Actividad" | "Gateway"
    private Long origenId;
    private String destinoTipo; // "Actividad" | "Gateway"
    private Long destinoId;
    private String condicion;   // opcional
}
