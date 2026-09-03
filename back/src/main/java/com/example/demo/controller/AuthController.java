package com.example.demo.controller;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    /**
     * Endpoint de Inicio de Sesión Web (HU-001)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String correo = body.get("correo");
        // El frontend envía "contrasena" (sin ñ); se mantiene "contraseña" por compatibilidad.
        String contrasena = body.get("contrasena") != null ? body.get("contrasena") : body.get("contraseña");

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

        if (!passwordEncoder.matches(contrasena, u.getContrasena())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", "Credenciales inválidas"));
        }

        String token = jwtService.generarToken(u.getIdUsuario(), u.getRol());
        return ResponseEntity.ok(Map.of(
            "mensaje", "Inicio de sesión exitoso",
            "token", token,
            "idUsuario", u.getIdUsuario(),
            "nombre", u.getNombre() + " " + u.getApellido(),
            "correo", u.getCorreo(),
            "rol", u.getRol()
        ));
    }

    /**
     * Solicitud de restablecimiento de contraseña (HU-002).
     * Genera un código de verificación numérico de un solo uso, vigente 15 minutos.
     * Nota: la app no tiene servicio de correo; el código se entrega en pantalla
     * al solicitante (en producción se enviaría por canal seguro).
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

        String codigo = String.format("%06d", (int) (Math.random() * 1000000));
        u.setTokenReset(codigo);
        u.setTokenExpiracion(LocalDateTime.now().plusMinutes(15));
        usuarioRepo.save(u);

        return ResponseEntity.ok(Map.of(
            "mensaje", "Código de verificación generado. Válido por 15 minutos.",
            "codigo", codigo
        ));
    }

    /**
     * Restablecimiento de contraseña usando el código de verificación generado previamente.
     */
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<?> restablecerContrasena(@RequestBody Map<String, String> body) {
        // El frontend envía "codigo"; se mantiene "token" por compatibilidad.
        String codigo = body.get("codigo") != null ? body.get("codigo") : body.get("token");
        String nuevaContrasena = body.get("nuevaContrasena");

        if (codigo == null || codigo.isBlank() || nuevaContrasena == null || nuevaContrasena.length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Código y nueva contraseña (mín. 6 caracteres) son obligatorios"));
        }

        Optional<Usuario> optUsuario = usuarioRepo.findByTokenReset(codigo);
        if (optUsuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "Código inválido"));
        }

        Usuario u = optUsuario.get();
        if (u.getTokenExpiracion() == null || u.getTokenExpiracion().isBefore(LocalDateTime.now())) {
            u.setTokenReset(null);
            u.setTokenExpiracion(null);
            usuarioRepo.save(u);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("mensaje", "El código ha expirado. Solicita uno nuevo."));
        }

        u.setContrasena(passwordEncoder.encode(nuevaContrasena));
        u.setTokenReset(null);
        u.setTokenExpiracion(null);
        u.setFechaActualizacion(LocalDateTime.now());
        usuarioRepo.save(u);

        return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida correctamente. Ya puedes iniciar sesión."));
    }
}
