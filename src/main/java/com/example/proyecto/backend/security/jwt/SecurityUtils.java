package com.example.proyecto.backend.security.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.example.proyecto.backend.entity.Role;

@Component
public class SecurityUtils {

    private final JwtUtil jwtUtil;

    public SecurityUtils(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    // =USER 

    public Long currentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getCredentials() == null) return null;

        String token = auth.getCredentials().toString();
        return jwtUtil.extractClaim(token, c -> c.get("userId", Long.class));
    }

    public String currentEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) return null;
        return auth.getName(); // subject del token = email
    }

    // ROLE 

    public Role currentRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getCredentials() == null) return null;

        String token = auth.getCredentials().toString();
        String roleName = jwtUtil.extractClaim(token, c -> c.get("role", String.class));

        if (roleName == null) return null;
        return Role.valueOf(roleName); // convierte "ADMIN" → Role.ADMIN
    }

    public boolean isAdmin() {
        return currentRole() == Role.ADMIN;
    }

    public boolean isEditor() {
        return currentRole() == Role.EDITOR;
    }

    public boolean isViewer() {
        return currentRole() == Role.VIEWER;
    }

    //EMPRESA 

    public Long currentCompanyId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getCredentials() == null) return null;

        String token = auth.getCredentials().toString();
        return jwtUtil.extractClaim(token, c -> c.get("companyId", Long.class));
    }
}
