package com.example.demo.controller;

import com.example.demo.dto.UsuarioDto;
import com.example.demo.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UsuarioService service;

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
}
