package com.example.proyecto.backend.security.jwt.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.proyecto.backend.dtos.LoginDTO;
import com.example.proyecto.backend.dtos.TokenDTO;
import com.example.proyecto.backend.dtos.UsuarioDTO;
import com.example.proyecto.backend.services.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth") // 👈 importante: tus rutas protegidas deberían ir con /api/**
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // ajusta si cambias el front
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    // LOGIN: devuelve el TokenDTO con el JWT
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO dto) {
        TokenDTO token = authService.login(dto);
        return ResponseEntity.ok(token);
    }

    // REGISTER: crea un usuario nuevo (sin necesitar JWT)
    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> register(@RequestBody UsuarioDTO dto) {
        UsuarioDTO creado = usuarioService.crearSinValidacion(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}