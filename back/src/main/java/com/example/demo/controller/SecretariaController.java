package com.example.demo.controller;

import com.example.demo.dto.CronogramaRequestDto;
import com.example.demo.model.CronogramaOperativo;
import com.example.demo.repository.CronogramaOperativoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cronograma")
public class SecretariaController {

    @Autowired
    private CronogramaOperativoRepository cronogramaRepo;

    /**
     * Endpoint para programar la categoría de medicamento en proceso cada día en room_911.
     * Un día puede tener varias categorías programadas; reactiva la pareja
     * (fecha, categoría) si ya existe (activa o inhabilitada).
     */
    @PostMapping
    public ResponseEntity<CronogramaOperativo> guardarCronograma(@RequestBody CronogramaRequestDto request) {
        LocalDate fecha = request.getFecha() != null ? request.getFecha() : LocalDate.now();
        Optional<CronogramaOperativo> existente = cronogramaRepo.findAllByFechaAndActivoTrue(fecha).stream()
                .filter(c -> request.getIdCategoria() != null && request.getIdCategoria().equals(c.getIdCategoria()))
                .findFirst();

        CronogramaOperativo cronograma;
        if (existente.isPresent()) {
            cronograma = existente.get();
            cronograma.setObservaciones(request.getObservaciones());
            cronograma.setActivo(true);
        } else {
            cronograma = new CronogramaOperativo(fecha, request.getIdCategoria(), request.getObservaciones());
        }

        return ResponseEntity.ok(cronogramaRepo.save(cronograma));
    }

    /**
     * Endpoint para editar cronograma por ID. Valida que la pareja (fecha, categoria)
     * destino no choque con otra programacion activa (indice unico parcial).
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> editarCronograma(@PathVariable Long id, @RequestBody CronogramaRequestDto request) {
        return cronogramaRepo.findById(id).<ResponseEntity<?>>map(c -> {
            LocalDate fechaDestino = request.getFecha() != null ? request.getFecha() : c.getFecha();
            Long categoriaDestino = request.getIdCategoria() != null ? request.getIdCategoria() : c.getIdCategoria();
            boolean duplicado = cronogramaRepo.findAllByFechaAndActivoTrue(fechaDestino).stream()
                    .anyMatch(o -> !o.getId().equals(id) && categoriaDestino != null && categoriaDestino.equals(o.getIdCategoria()));
            if (duplicado) {
                return ResponseEntity.badRequest().body(Map.of(
                        "mensaje", "Ya existe una programación activa para esa fecha y categoría"));
            }
            if (request.getFecha() != null) c.setFecha(request.getFecha());
            if (request.getIdCategoria() != null) c.setIdCategoria(request.getIdCategoria());
            if (request.getObservaciones() != null) c.setObservaciones(request.getObservaciones());
            return ResponseEntity.ok(cronogramaRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Soft Delete: Inhabilita la programación diaria sin borrar el registro de la BD.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CronogramaOperativo> inhabilitarCronograma(@PathVariable Long id) {
        return cronogramaRepo.findById(id).map(c -> {
            c.setActivo(false);
            return ResponseEntity.ok(cronogramaRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Reactiva una programación inhabilitada (soft delete inverso).
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<?> reactivarCronograma(@PathVariable Long id) {
        return cronogramaRepo.findById(id).<ResponseEntity<?>>map(c -> {
            boolean duplicado = cronogramaRepo.findAllByFechaAndActivoTrue(c.getFecha()).stream()
                    .anyMatch(o -> !o.getId().equals(id) && c.getIdCategoria() != null && c.getIdCategoria().equals(o.getIdCategoria()));
            if (duplicado) {
                return ResponseEntity.badRequest().body(Map.of(
                        "mensaje", "Ya existe una programación activa para esa fecha y categoría"));
            }
            c.setActivo(true);
            return ResponseEntity.ok(cronogramaRepo.save(c));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para obtener la programación activa de hoy.
     */
    @GetMapping("/hoy")
    public ResponseEntity<CronogramaOperativo> obtenerCronogramaHoy() {
        return cronogramaRepo.findByFechaAndActivoTrue(LocalDate.now())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Endpoint para listar todo el cronograma operativo.
     */
    @GetMapping
    public ResponseEntity<List<CronogramaOperativo>> listarCronograma() {
        return ResponseEntity.ok(cronogramaRepo.findAll());
    }
}
