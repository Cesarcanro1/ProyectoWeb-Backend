package com.example.proyecto.backend.security.jwt;

import java.io.IOException;
import java.util.Set;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService uds;

    // 👇 rutas públicas que NO requieren token
    private static final Set<String> WHITE_LIST = Set.of(
            "/auth/login",
            "/api/public",            // base
            "/api/public/",           // con slash
            "/api/public/signup"      // endpoint exacto
            // agrega aquí /health, /ping si quieres
    );

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain
    ) throws ServletException, IOException {

        // 0) Preflight CORS: siempre dejar pasar
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 1) Normaliza el path (quita contextPath si existe)
        String uri = request.getRequestURI();                 // ej: /api/public/signup
        String ctx = request.getContextPath();                // ej: /backend (si usas)
        String path = (ctx != null && !ctx.isEmpty() && uri.startsWith(ctx))
                ? uri.substring(ctx.length())
                : uri;

        // 2) Si es ruta pública -> seguir sin tocar auth
        if (isWhitelisted(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 3) Si ya hay autenticación en el contexto -> seguir
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        // 4) Lee Authorization
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // sin token -> dejar pasar y SecurityConfig decidirá (401/403 si corresponde)
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        try {
            final String username = jwtUtil.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                var user = uds.loadUserByUsername(username);
                if (jwtUtil.isValid(token, user)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                            user, null, user.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception ignored) {
            // no rompas el flujo: deja que SecurityConfig responda 401/403 si toca
        }

        chain.doFilter(request, response);
    }

    private boolean isWhitelisted(String path) {
        if (path == null) return false;
        return WHITE_LIST.stream().anyMatch(w ->
                path.equals(w) || path.startsWith(w + "/")
        );
    }
}
