package org.example.sala911.controlador;
import org.example.sala911.servicio.AccesoServicio; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/registros-acceso") @CrossOrigin(origins="*")
public class RegistroAccesoControlador {
 private final AccesoServicio servicio; public RegistroAccesoControlador(AccesoServicio s){servicio=s;}
 @GetMapping public Object todos(){return servicio.todos();}
 @GetMapping("/usuario/{id}") public Object usuario(@PathVariable String id){return servicio.porUsuario(id);}
}
