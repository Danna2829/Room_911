package com.example.demo.security;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Filtro stateless: valida el header Authorization Bearer y establece la
 * autenticacion con el rol del usuario como autoridad.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ") && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtService.validarToken(header.substring(7));
                String idUsuario = claims.getSubject();
                Optional<Usuario> usuario = usuarioRepo.findById(idUsuario);
                // El usuario debe existir y seguir activo (soft delete invalida tokens)
                if (usuario.isPresent() && Boolean.TRUE.equals(usuario.get().getEstado())) {
                    String rol = usuario.get().getRol() != null ? usuario.get().getRol() : "OPERARIO";
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            idUsuario, null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + rol)));
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
                // Token invalido o expirado: se deja sin autenticacion y el filtro de autorizacion responde 401.
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(request, response);
    }
}
