package org.example.sala911.controlador;
import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestController @RequestMapping("/api") public class SaludControlador { @GetMapping("/salud") public Object salud(){return Map.of("servicio","room-911","estado","ok");} }
