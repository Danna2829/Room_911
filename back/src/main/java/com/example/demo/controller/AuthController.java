package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    /**
     * Endpoint de Inicio de Sesión Web (HU-001)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        String contrasena = body.get("contraseña");

        if (correo == null || contrasena == null) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Correo y contraseña son obligatorios"));
        }

        Optional<Usuario> optUsuario = usuarioRepo.findByCorreo(correo);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Credenciales inválidas"));
        }

        Usuario u = optUsuario.get();
        if (Boolean.FALSE.equals(u.getEstado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Su cuenta se encuentra inactiva. Contacte al Administrador"));
        }

        if (!contrasena.equals(u.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Credenciales inválidas"));
        }

        return ResponseEntity.ok(Map.of(
            "mensaje", "Inicio de sesión exitoso",
            "idUsuario", u.getIdUsuario(),
            "nombre", u.getNombre() + " " + u.getApellido(),
            "correo", u.getCorreo(),
            "rol", u.getRol()
        ));
    }

    /**
     * Endpoint de Solicitud de Restablecimiento de Contraseña (HU-002)
     */
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        Optional<Usuario> optUsuario = usuarioRepo.findByCorreo(correo);

        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "No existe una cuenta asociada a este correo"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Enlace de recuperación generado para el correo: " + correo));
    }
}
