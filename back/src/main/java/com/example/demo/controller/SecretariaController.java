package com.example.demo.controller;

import com.example.demo.dto.CronogramaRequestDto;
import com.example.demo.model.CronogramaOperativo;
import com.example.demo.repository.CronogramaOperativoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cronograma")
public class SecretariaController {

    @Autowired
    private CronogramaOperativoRepository cronogramaRepo;

    /**
     * Endpoint para programar la categoría de medicamento en proceso cada día en room_911.
     */
    @PostMapping
    public ResponseEntity<CronogramaOperativo> guardarCronograma(@RequestBody CronogramaRequestDto request) {
        LocalDate fecha = request.getFecha() != null ? request.getFecha() : LocalDate.now();
        Optional<CronogramaOperativo> existente = cronogramaRepo.findByFecha(fecha);

        CronogramaOperativo cronograma;
        if (existente.isPresent()) {
            cronograma = existente.get();
            cronograma.setIdCategoria(request.getIdCategoria());
            cronograma.setObservaciones(request.getObservaciones());
            cronograma.setActivo(true);
        } else {
            cronograma = new CronogramaOperativo(fecha, request.getIdCategoria(), request.getObservaciones());
        }

        return ResponseEntity.ok(cronogramaRepo.save(cronograma));
    }

    /**
     * Endpoint para editar cronograma por ID.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CronogramaOperativo> editarCronograma(@PathVariable Long id, @RequestBody CronogramaRequestDto request) {
        return cronogramaRepo.findById(id).map(c -> {
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
