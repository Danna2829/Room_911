package org.example.sala911.controlador;
import org.example.sala911.dto.CronogramaSolicitud; import org.example.sala911.servicio.CronogramaServicio; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/cronogramas") @CrossOrigin(origins="*")
public class CronogramaControlador { private final CronogramaServicio servicio; public CronogramaControlador(CronogramaServicio s){servicio=s;} @GetMapping public Object todos(){return servicio.todos();} @PostMapping public Object crear(@RequestBody CronogramaSolicitud s){return servicio.guardar(s);} @DeleteMapping("/{id}") public void eliminar(@PathVariable Long id){servicio.eliminar(id);} }
