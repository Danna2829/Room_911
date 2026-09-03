package com.example.demo.config;

import com.example.demo.dto.UsuarioDto;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejo global de errores: mensajes JSON accionables sin exponer stacktraces.
 * Los controladores con manejo propio (mas especifico) conservan su comportamiento.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage() != null ? e.getMessage() : "Solicitud inválida"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> validacion(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(f -> f.getField() + ": " + f.getDefaultMessage())
                .orElse("Datos inválidos");
        return ResponseEntity.badRequest().body(Map.of("mensaje", msg));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> integridad(DataIntegrityViolationException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", "La operación viola una restricción de datos (valor duplicado o referenciado)"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> error(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Error interno del servidor"));
    }
}
