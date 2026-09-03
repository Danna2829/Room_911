package com.example.demo.controller;

import com.example.demo.model.CategoriaMedicamento;
import com.example.demo.repository.CategoriaMedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaMedicamentoRepository categoriaRepo;

    @GetMapping
    public ResponseEntity<List<CategoriaMedicamento>> listarCategorias() {
        return ResponseEntity.ok(categoriaRepo.findAll());
    }

    @PostMapping
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaMedicamento cat) {
        if (cat.getCodigo() == null || cat.getCodigo().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "El código es obligatorio"));
        }
        if (categoriaRepo.findByCodigo(cat.getCodigo()).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe una categoría con el código " + cat.getCodigo()));
        }
        cat.setActivo(true);
        return ResponseEntity.ok(categoriaRepo.save(cat));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editarCategoria(@PathVariable Long id, @RequestBody CategoriaMedicamento datos) {
        return categoriaRepo.findById(id).<ResponseEntity<?>>map(c -> {
            if (datos.getCodigo() != null) {
                var existente = categoriaRepo.findByCodigo(datos.getCodigo());
                if (existente.isPresent() && !existente.get().getId().equals(id)) {
                    return ResponseEntity.badRequest().body(Map.of("mensaje", "Ya existe otra categoría con el código " + datos.getCodigo()));
                }
                c.setCodigo(datos.getCodigo());
            }
            if (datos.getNombre() != null) c.setNombre(datos.getNombre());
            if (datos.getDescripcion() != null) c.setDescripcion(datos.getDescripcion());
            if (datos.getEsRestringido() != null) c.setEsRestringido(datos.getEsRestringido());
            if (datos.getActivo() != null) c.setActivo(datos.getActivo());
            return ResponseEntity.ok(categoriaRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Soft Delete: Inhabilita la categoría sin borrar el registro físico de la BD.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CategoriaMedicamento> inhabilitarCategoria(@PathVariable Long id) {
        return categoriaRepo.findById(id).map(c -> {
            c.setActivo(false);
            return ResponseEntity.ok(categoriaRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/reactivar")
    public ResponseEntity<CategoriaMedicamento> reactivarCategoria(@PathVariable Long id) {
        return categoriaRepo.findById(id).map(c -> {
            c.setActivo(true);
            return ResponseEntity.ok(categoriaRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }
}
