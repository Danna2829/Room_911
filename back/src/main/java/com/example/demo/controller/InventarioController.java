package com.example.demo.controller;

import com.example.demo.model.InventarioMedicamento;
import com.example.demo.repository.InventarioMedicamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioMedicamentoRepository inventarioRepo;

    @GetMapping
    public ResponseEntity<List<InventarioMedicamento>> consultarInventario() {
        return ResponseEntity.ok(inventarioRepo.findAll());
    }

    @PostMapping("/entrada")
    public ResponseEntity<InventarioMedicamento> registrarEntrada(@RequestBody InventarioMedicamento inv) {
        inv.setTipoMovimiento("ENTRADA");
        inv.setTimestamp(LocalDateTime.now());
        inv.setActivo(true);
        return ResponseEntity.ok(inventarioRepo.save(inv));
    }

    @PostMapping("/salida")
    public ResponseEntity<InventarioMedicamento> registrarSalida(@RequestBody InventarioMedicamento inv) {
        inv.setTipoMovimiento("SALIDA");
        inv.setTimestamp(LocalDateTime.now());
        inv.setActivo(true);
        return ResponseEntity.ok(inventarioRepo.save(inv));
    }

    /**
     * Soft Delete / Inhabilitación: Anula lógicamente el movimiento sin borrar de la BD.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<InventarioMedicamento> inhabilitarMovimiento(@PathVariable Long id) {
        return inventarioRepo.findById(id).map(inv -> {
            inv.setActivo(false);
            return ResponseEntity.ok(inventarioRepo.save(inv));
        }).orElse(ResponseEntity.notFound().build());
    }

    /**
     * Reactiva un movimiento previamente anulado (soft delete inverso).
     */
    @PutMapping("/{id}/reactivar")
    public ResponseEntity<InventarioMedicamento> reactivarMovimiento(@PathVariable Long id) {
        return inventarioRepo.findById(id).map(inv -> {
            inv.setActivo(true);
            return ResponseEntity.ok(inventarioRepo.save(inv));
        }).orElse(ResponseEntity.notFound().build());
    }
}
