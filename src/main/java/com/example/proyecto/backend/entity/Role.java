package com.example.proyecto.backend.entity;

public enum Role {
    ADMIN,      // controla empresa y usuarios
    EDITOR,     // puede editar procesos y actividades
    VIEWER,        // solo lectura
    USER
}