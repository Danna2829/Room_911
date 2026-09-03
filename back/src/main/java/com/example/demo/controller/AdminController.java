package com.example.demo.controller;

import com.example.demo.dto.UsuarioDto;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UsuarioService service;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @PostMapping("/crear-usuario")
    public ResponseEntity<?> crearUsuario(@RequestBody UsuarioDto usuario) {
        try {
            return ResponseEntity.ok(service.crearUsuario(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    /**
     * Carga masiva de usuarios a partir de una lista (la UI parsea el CSV).
     * Devuelve el resultado por fila sin abortar el lote completo.
     */
    @PostMapping("/crear-usuarios")
    public ResponseEntity<List<Map<String, Object>>> crearUsuarios(@RequestBody List<UsuarioDto> usuarios) {
        return ResponseEntity.ok(service.crearUsuarios(usuarios));
    }

    /**
     * Reactiva un usuario inhabilitado (borrado logico inverso).
     */
    @PutMapping("/usuario/{id}/activar")
    public ResponseEntity<?> activarUsuario(@PathVariable String id) {
        try {
            return ResponseEntity.ok(service.activarUsuario(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/listar-usuarios")
    public ResponseEntity<List<UsuarioDto>> listarUsuarios() {
        return ResponseEntity.ok(service.listarUsuarios());
    }

    @GetMapping("/usuario/{id}")
    public ResponseEntity<UsuarioDto> obtenerUsuario(@PathVariable String id) {
        UsuarioDto dto = service.obtenerPorId(id);
        if (dto != null) {
            return ResponseEntity.ok(dto);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/editar-usuario/{id}")
    public ResponseEntity<?> editarUsuario(@PathVariable String id, @RequestBody UsuarioDto datos) {
        try {
            return ResponseEntity.ok(service.editarUsuario(id, datos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar-usuario/{id}")
    public ResponseEntity<?> eliminarUsuario(@PathVariable String id) {
        try {
            service.eliminarUsuario(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", e.getMessage()));
        }
    }

    /**
     * Restablecimiento de contraseña por parte de un SUPERADMINISTRADOR.
     * Genera una contraseña temporal y la devuelve para entrega fuera de banda.
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> body) {
        String idUsuario = body.get("idUsuario");
        String solicitanteId = body.get("solicitanteId");

        if (solicitanteId != null && !solicitanteId.isBlank()) {
            Optional<Usuario> s = usuarioRepo.findById(solicitanteId);
            if (s.isEmpty() || !"SUPERADMINISTRADOR".equalsIgnoreCase(s.get().getRol())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("mensaje", "Solo un SUPERADMINISTRADOR puede restablecer contraseñas."));
            }
        }

        if (idUsuario == null || idUsuario.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "idUsuario es obligatorio"));
        }

        Usuario u = usuarioRepo.findById(idUsuario).orElse(null);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "Usuario no encontrado"));
        }

        String tempPassword = "Temp-" + UUID.randomUUID().toString().substring(0, 6);
        u.setContrasena(passwordEncoder.encode(tempPassword));
        u.setTokenReset(null);
        u.setTokenExpiracion(null);
        u.setFechaActualizacion(LocalDateTime.now());
        usuarioRepo.save(u);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Contraseña restablecida por el Superadministrador.",
            "idUsuario", idUsuario,
            "tempPassword", tempPassword
        ));
    }
}
