package org.example.sala911.controlador;
import org.example.sala911.dto.AccesoSolicitud; import org.example.sala911.servicio.AccesoServicio;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.time.LocalDate;
@RestController @RequestMapping("/api/accesos") @CrossOrigin(origins="*")
public class AccesoControlador {
 private final AccesoServicio servicio; public AccesoControlador(AccesoServicio s){servicio=s;}
 @PostMapping("/evaluar") public Object evaluar(@RequestBody AccesoSolicitud solicitud){return servicio.evaluar(solicitud);}
 @GetMapping public Object todos(){return servicio.todos();}
 @GetMapping("/filtrar") public Object filtrar(@RequestParam(required=false) String resultado,@RequestParam(required=false) String idUsuario,@RequestParam(required=false) LocalDate desde,@RequestParam(required=false) LocalDate hasta){return servicio.filtrar(resultado,idUsuario,desde,hasta);}
 @GetMapping(value="/exportar.csv",produces="text/csv") public ResponseEntity<String> exportar(@RequestParam(required=false) String resultado,@RequestParam(required=false) String idUsuario,@RequestParam(required=false) LocalDate desde,@RequestParam(required=false) LocalDate hasta){return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=room-911-accesos.csv").body(servicio.csv(resultado,idUsuario,desde,hasta));}
 @GetMapping("/usuario/{id}") public Object usuario(@PathVariable String id){return servicio.porUsuario(id);}
}
