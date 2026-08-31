package org.example.sala911.controlador;

import org.example.sala911.entidad.Perfil;
import org.example.sala911.repositorio.PerfilRepositorio;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/perfiles")
@CrossOrigin(origins = "*")
public class PerfilControlador {
    private final PerfilRepositorio repositorio;

    public PerfilControlador(PerfilRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    @GetMapping
    public Object todos() {
        return repositorio.findAll();
    }
}
