package com.example.demo.config;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Migracion idempotente: cifra con BCrypt las contrasenas que persistan en
 * texto plano (semillas iniciales o datos previos al esquema de seguridad).
 */
@Component
public class PasswordMigrationRunner implements ApplicationRunner {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        List<Usuario> enTextoPlano = usuarioRepo.findAll().stream()
                .filter(u -> u.getContrasena() != null && !u.getContrasena().startsWith("$2"))
                .toList();
        for (Usuario u : enTextoPlano) {
            u.setContrasena(passwordEncoder.encode(u.getContrasena()));
            usuarioRepo.save(u);
        }
    }
}
