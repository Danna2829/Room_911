package com.example.demo.service;

import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.PerfilOperario;
import com.example.demo.model.Usuario;
import com.example.demo.repository.PerfilOperarioRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repo;

    @Autowired
    private PerfilOperarioRepository perfilRepo;

    @Transactional
    public UsuarioDto crearUsuario(UsuarioDto dto) {
        // 1. Generar ID de expediente interno con formato estándar EMP-XXXX (ej. EMP-8821)
        String idUsuario = dto.getIdUsuario();
        if (idUsuario == null || idUsuario.trim().isEmpty()) {
            idUsuario = generarNuevoIdEmp();
        } else if (!idUsuario.toUpperCase().startsWith("EMP-")) {
            idUsuario = "EMP-" + idUsuario.replaceAll("[^0-9]", "");
            if (idUsuario.equals("EMP-")) {
                idUsuario = generarNuevoIdEmp();
            }
        }
        dto.setIdUsuario(idUsuario);

        // 2. Construir y guardar entidad Usuario
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(idUsuario);
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setCorreo(dto.getCorreo());
        usuario.setRol(dto.getRol() != null ? dto.getRol().toUpperCase() : "OPERARIO");
        usuario.setContrasena(dto.getContrasena() != null ? dto.getContrasena() : "pass123");
        usuario.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        usuario.setFechaCreacion(LocalDateTime.now());
        usuario.setFechaActualizacion(LocalDateTime.now());

        Usuario savedUser = repo.save(usuario);

        // 3. Si es OPERARIO o se especifica nivelAcceso, persistir PerfilOperario
        Integer nivel = dto.getNivelAcceso();
        if ("OPERARIO".equalsIgnoreCase(savedUser.getRol())) {
            if (nivel == null || nivel < 1 || nivel > 3) {
                nivel = 1;
            }
            String desc = obtenerDescripcionNivel(nivel);
            Optional<PerfilOperario> perfilOpt = perfilRepo.findByIdUsuario(savedUser.getIdUsuario());
            PerfilOperario perfil;
            if (perfilOpt.isPresent()) {
                perfil = perfilOpt.get();
                perfil.setNivelAcceso(nivel);
                perfil.setDescripcion(desc);
            } else {
                perfil = new PerfilOperario(savedUser.getIdUsuario(), nivel, desc);
            }
            perfilRepo.save(perfil);
            dto.setNivelAcceso(nivel);
            dto.setDescripcionPerfil(desc);
        }

        dto.setFechaCreacion(savedUser.getFechaCreacion());
        dto.setFechaActualizacion(savedUser.getFechaActualizacion());
        dto.setEstado(savedUser.getEstado());
        return dto;
    }

    public List<UsuarioDto> listarUsuarios() {
        List<Usuario> usuarios = repo.findAll();
        List<UsuarioDto> dtos = new ArrayList<>();

        for (Usuario u : usuarios) {
            UsuarioDto dto = new UsuarioDto();
            dto.setIdUsuario(u.getIdUsuario());
            dto.setNombre(u.getNombre());
            dto.setApellido(u.getApellido());
            dto.setCorreo(u.getCorreo());
            dto.setRol(u.getRol());
            dto.setEstado(u.getEstado());
            dto.setFechaCreacion(u.getFechaCreacion());
            dto.setFechaActualizacion(u.getFechaActualizacion());

            Optional<PerfilOperario> perfilOpt = perfilRepo.findByIdUsuario(u.getIdUsuario());
            if (perfilOpt.isPresent()) {
                dto.setNivelAcceso(perfilOpt.get().getNivelAcceso());
                dto.setDescripcionPerfil(perfilOpt.get().getDescripcion());
            } else if ("ADMINISTRADOR".equalsIgnoreCase(u.getRol())) {
                dto.setNivelAcceso(3);
                dto.setDescripcionPerfil("Administrador - Acceso Total");
            } else if ("GUARDIA_SEGURIDAD".equalsIgnoreCase(u.getRol())) {
                dto.setNivelAcceso(3);
                dto.setDescripcionPerfil("Guardia de Seguridad - Supervisión");
            }

            dtos.add(dto);
        }

        return dtos;
    }

    public UsuarioDto obtenerPorId(String id) {
        Usuario u = repo.findById(id).orElse(null);
        if (u == null) return null;

        UsuarioDto dto = new UsuarioDto();
        dto.setIdUsuario(u.getIdUsuario());
        dto.setNombre(u.getNombre());
        dto.setApellido(u.getApellido());
        dto.setCorreo(u.getCorreo());
        dto.setRol(u.getRol());
        dto.setEstado(u.getEstado());
        dto.setFechaCreacion(u.getFechaCreacion());
        dto.setFechaActualizacion(u.getFechaActualizacion());

        perfilRepo.findByIdUsuario(u.getIdUsuario()).ifPresent(p -> {
            dto.setNivelAcceso(p.getNivelAcceso());
            dto.setDescripcionPerfil(p.getDescripcion());
        });

        return dto;
    }

    @Transactional
    public UsuarioDto editarUsuario(String id, UsuarioDto datos) {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        if (datos.getNombre() != null) usuario.setNombre(datos.getNombre());
        if (datos.getApellido() != null) usuario.setApellido(datos.getApellido());
        if (datos.getCorreo() != null) usuario.setCorreo(datos.getCorreo());
        if (datos.getRol() != null) usuario.setRol(datos.getRol().toUpperCase());
        if (datos.getContrasena() != null && !datos.getContrasena().trim().isEmpty()) {
            usuario.setContrasena(datos.getContrasena());
        }
        if (datos.getEstado() != null) usuario.setEstado(datos.getEstado());
        usuario.setFechaActualizacion(LocalDateTime.now());
        Usuario savedUser = repo.save(usuario);

        if (datos.getNivelAcceso() != null || "OPERARIO".equalsIgnoreCase(savedUser.getRol())) {
            int nivel = datos.getNivelAcceso() != null ? datos.getNivelAcceso() : 1;
            String desc = obtenerDescripcionNivel(nivel);
            Optional<PerfilOperario> perfilOpt = perfilRepo.findByIdUsuario(savedUser.getIdUsuario());
            PerfilOperario perfil = perfilOpt.orElseGet(() -> new PerfilOperario(savedUser.getIdUsuario(), nivel, desc));
            perfil.setNivelAcceso(nivel);
            perfil.setDescripcion(desc);
            perfilRepo.save(perfil);
            datos.setNivelAcceso(nivel);
            datos.setDescripcionPerfil(desc);
        }

        datos.setIdUsuario(savedUser.getIdUsuario());
        datos.setRol(savedUser.getRol());
        datos.setEstado(savedUser.getEstado());
        datos.setFechaActualizacion(savedUser.getFechaActualizacion());
        return datos;
    }

    /**
     * Soft Delete / Inhabilitación Lógica: Nunca elimina registros físicos de la BD.
     */
    @Transactional
    public void eliminarUsuario(String id) {
        Usuario usuario = repo.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        usuario.setEstado(false);
        usuario.setFechaActualizacion(LocalDateTime.now());
        repo.save(usuario);
    }

    private String generarNuevoIdEmp() {
        for (int i = 0; i < 100; i++) {
            String candidate = "EMP-" + String.format("%04d", (int)(Math.random() * 9000 + 1000));
            if (!repo.existsById(candidate)) {
                return candidate;
            }
        }
        return "EMP-" + System.currentTimeMillis() % 10000;
    }

    private String obtenerDescripcionNivel(int nivel) {
        switch (nivel) {
            case 1: return "Operario Nivel 1 - Acceso a Tipo 1 y 2";
            case 2: return "Operario Nivel 2 - Acceso a Tipo 2 y 5";
            case 3: return "Operario Nivel 3 - Acceso Global (Incluye Tipo 4 Restringido)";
            default: return "Operario Nivel 1 - Acceso Estándar";
        }
    }
}
