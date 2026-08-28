package com.example.demo.controller;

import com.example.demo.dto.AccesoRequestDto;
import com.example.demo.dto.AccesoResponseDto;
import com.example.demo.model.RegistroAuditoria;
import com.example.demo.repository.RegistroAuditoriaRepository;
import com.example.demo.service.ABACEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/acceso")
public class AccesoController {

    @Autowired
    private ABACEngineService abacEngineService;

    @Autowired
    private RegistroAuditoriaRepository auditoriaRepo;

    /**
     * Endpoint del Simulador de Garita / Torniquete Táctil.
     * Evalúa el acceso de ENTRADA o SALIDA de un operario mediante su ID Interno.
     */
    @PostMapping("/evaluar")
    public ResponseEntity<AccesoResponseDto> evaluarAcceso(@RequestBody AccesoRequestDto request) {
        AccesoResponseDto respuesta = abacEngineService.evaluarAcceso(request);
        return ResponseEntity.ok(respuesta);
    }

    /**
     * Endpoint para el Panel de Guardia de Seguridad (Monitoreo en Tiempo Real).
     */
    @GetMapping("/monitor")
    public ResponseEntity<List<RegistroAuditoria>> monitorearAccesos() {
        return ResponseEntity.ok(auditoriaRepo.findAll());
    }

    /**
     * Endpoint para consultar historial de auditoría de accesos.
     */
    @GetMapping("/historial")
    public ResponseEntity<List<RegistroAuditoria>> consultarHistorial() {
        return ResponseEntity.ok(auditoriaRepo.findAll());
    }
}
