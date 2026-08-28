package com.example.demo.controller;

import com.example.demo.dto.SuspensionRequestDto;
import com.example.demo.model.SuspensionPermiso;
import com.example.demo.repository.SuspensionPermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/guardia")
public class GuardiaController {

    @Autowired
    private SuspensionPermisoRepository suspensionRepo;

    /**
     * Endpoint para crear una suspensión individual (inmediata o por rango de fechas) por Guardia.
     */
    @PostMapping("/suspender")
    public ResponseEntity<SuspensionPermiso> crearSuspension(@RequestBody SuspensionRequestDto request) {
        LocalDateTime inicio = request.getFechaInicio() != null ? request.getFechaInicio() : LocalDateTime.now();
        SuspensionPermiso suspension = new SuspensionPermiso(
                request.getIdUsuario(),
                inicio,
                request.getFechaFin(),
                request.getMotivo() != null ? request.getMotivo() : "SUSPENSIÓN TEMPORAL",
                true
        );
        return ResponseEntity.ok(suspensionRepo.save(suspension));
    }

    /**
     * Endpoint para listar todas las suspensiones registradas.
     */
    @GetMapping("/suspensiones")
    public ResponseEntity<List<SuspensionPermiso>> listarSuspensiones() {
        return ResponseEntity.ok(suspensionRepo.findAll());
    }

    /**
     * Endpoint para desactivar / revocar una suspensión.
     */
    @PutMapping("/suspensiones/{id}/desactivar")
    public ResponseEntity<SuspensionPermiso> desactivarSuspension(@PathVariable Long id) {
        return suspensionRepo.findById(id).map(s -> {
            s.setActivo(false);
            return ResponseEntity.ok(suspensionRepo.save(s));
        }).orElse(ResponseEntity.notFound().build());
    }
}
