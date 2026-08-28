package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

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
     * Solicitud de restablecimiento de contraseña (HU-002).
     * Genera un token de un solo uso con vigencia de 15 minutos.
     * Nota: la app no tiene servicio de correo; el token se entrega al solicitante
     * para usarlo en /restablecer-contrasena (en producción se enviaría por canal seguro).
     */
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<?> recuperarContrasena(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        if (correo == null || correo.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El correo es obligatorio"));
        }

        Optional<Usuario> optUsuario = usuarioRepo.findByCorreo(correo);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("mensaje", "No existe una cuenta asociada a este correo"));
        }

        Usuario u = optUsuario.get();
        if (Boolean.FALSE.equals(u.getEstado())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Su cuenta está inactiva. Contacte al Superadministrador"));
        }

        String token = UUID.randomUUID().toString();
        u.setTokenReset(token);
        u.setTokenExpiracion(LocalDateTime.now().plusMinutes(15));
        usuarioRepo.save(u);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Se generó un token de recuperación. Úsalo en la opción 'Restablecer contraseña'.",
            "token", token
        ));
    }

    /**
     * Restablecimiento de contraseña usando el token generado previamente.
     */
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<?> restablecerContrasena(@RequestBody Map<String, String> body) {
        String token = body.get("token");
        String nuevaContrasena = body.get("nuevaContrasena");

        if (token == null || token.isBlank() || nuevaContrasena == null || nuevaContrasena.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Token y nueva contraseña (mín. 6 caracteres) son obligatorios"));
        }

        Optional<Usuario> optUsuario = usuarioRepo.findByTokenReset(token);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "Token inválido"));
        }

        Usuario u = optUsuario.get();
        if (u.getTokenExpiracion() == null || u.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            u.setTokenReset(null);
            u.setTokenExpiracion(null);
            usuarioRepo.save(u);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "El token ha expirado. Solicita uno nuevo."));
        }

        u.setContrasena(nuevaContrasena);
        u.setTokenReset(null);
        u.setTokenExpiracion(null);
        u.setFechaActualizacion(LocalDateTime.now());
        usuarioRepo.save(u);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida correctamente. Ya puedes iniciar sesión."));
    }
}
