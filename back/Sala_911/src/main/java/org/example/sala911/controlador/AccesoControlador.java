package org.example.sala911.controlador;
import org.example.sala911.dto.AccesoSolicitud; import org.example.sala911.servicio.AccesoServicio;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/accesos") @CrossOrigin(origins="*")
public class AccesoControlador {
 private final AccesoServicio servicio; public AccesoControlador(AccesoServicio s){servicio=s;}
 @PostMapping("/evaluar") public Object evaluar(@RequestBody AccesoSolicitud solicitud){return servicio.evaluar(solicitud);}
 @GetMapping public Object todos(){return servicio.todos();}
 @GetMapping("/usuario/{id}") public Object usuario(@PathVariable String id){return servicio.porUsuario(id);}
}
