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

    @PostMapping("/crear-usuario")
    public ResponseEntity<UsuarioDto> crearUsuario(@RequestBody UsuarioDto usuario) {
        return ResponseEntity.ok(service.crearUsuario(usuario));
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
    public ResponseEntity<UsuarioDto> editarUsuario(@PathVariable String id, @RequestBody UsuarioDto datos) {
        return ResponseEntity.ok(service.editarUsuario(id, datos));
    }

    @DeleteMapping("/eliminar-usuario/{id}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String id) {
        service.eliminarUsuario(id);
        return ResponseEntity.noContent().build();
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
        u.setContrasena(tempPassword);
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
