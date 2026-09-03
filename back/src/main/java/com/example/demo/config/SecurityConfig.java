package com.example.demo.config;

import com.example.demo.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://127.0.0.1:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Autenticacion y preflight CORS
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Garita / torniquete: cualquier usuario autenticado (operario valida su expediente)
                        .requestMatchers("/api/acceso/**").authenticated()

                        // Administracion de usuarios: solo administradores
                        .requestMatchers("/api/admin/**").hasAnyRole("ADMINISTRADOR", "SUPERADMINISTRADOR")

                        // Seguridad: guardia y administradores
                        .requestMatchers("/api/guardia/**").hasAnyRole("GUARDIA_SEGURIDAD", "ADMINISTRADOR", "SUPERADMINISTRADOR")

                        // Escrituras de cronograma y catalogo: secretaria y administradores
                        .requestMatchers(HttpMethod.POST, "/api/cronograma/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/cronograma/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/cronograma/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/categorias/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/categorias/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/categorias/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")

                        // Inventario: movimientos solo para secretaria y administradores
                        .requestMatchers(HttpMethod.POST, "/api/inventario/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/inventario/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/inventario/**").hasAnyRole("SECRETARIA", "ADMINISTRADOR", "SUPERADMINISTRADOR")

                        // Reportes: roles de supervision y gestion
                        .requestMatchers("/api/reportes/**").hasAnyRole("ADMINISTRADOR", "SUPERADMINISTRADOR", "GUARDIA_SEGURIDAD", "SECRETARIA")

                        // Panel General: roles de gestion
                        .requestMatchers("/api/dashboard/**").hasAnyRole("ADMINISTRADOR", "SUPERADMINISTRADOR", "SECRETARIA")

                        // Lecturas generales y cualquier otro endpoint: autenticados
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        // Sin token o token invalido: 401 JSON (el frontend cierra sesion).
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"mensaje\":\"Sesión no válida o expirada\"}");
                        })
                        // Token valido pero sin permisos: 403 JSON.
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(403);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"mensaje\":\"No tienes permisos para esta operación\"}");
                        })
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
