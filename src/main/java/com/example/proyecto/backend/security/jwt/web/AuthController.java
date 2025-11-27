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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")  // Ajusta según tu front
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    // LOGIN: devuelve TokenDTO con JWT
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO dto) {
        TokenDTO token = authService.login(dto);
        return ResponseEntity.ok(token);
    }

    // REGISTER: crea un usuario sin necesidad de token
    @PostMapping("/register")
    public ResponseEntity<UsuarioDTO> register(@RequestBody UsuarioDTO dto) {
        UsuarioDTO creado = usuarioService.crear(dto); // usamos crear normal
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }
}
