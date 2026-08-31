package org.example.sala911.controlador;
import org.example.sala911.dto.*; import org.example.sala911.entidad.Usuario; import org.example.sala911.servicio.UsuarioServicio;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.time.LocalDate; import java.util.Map;
@RestController @RequestMapping("/api/usuarios") @CrossOrigin(origins="*")
public class UsuarioControlador {
 private final UsuarioServicio servicio; public UsuarioControlador(UsuarioServicio s){servicio=s;}
 @GetMapping public Object todos(){return servicio.todos();}
 @GetMapping("/{id}") public ResponseEntity<?> uno(@PathVariable String id){try{return ResponseEntity.ok(servicio.identificar(id));}catch(IllegalArgumentException e){return ResponseEntity.notFound().build();}}
 @PostMapping("/identificar") public ResponseEntity<?> identificar(@RequestBody IdentificacionSolicitud s){try{return ResponseEntity.ok(servicio.identificar(s.idUsuario()));}catch(IllegalArgumentException e){return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje",e.getMessage()));}}
 @PostMapping("/login") public ResponseEntity<?> login(@RequestBody IdentificacionSolicitud s){return identificar(s);}
 @PostMapping public ResponseEntity<?> crear(@RequestBody Usuario u){return ResponseEntity.status(HttpStatus.CREATED).body(servicio.guardar(u));}
 @PatchMapping("/{id}/estado") public ResponseEntity<?> estado(@PathVariable String id,@RequestParam boolean activo,@RequestParam(required=false) LocalDate desde,@RequestParam(required=false) LocalDate hasta){try{return ResponseEntity.ok(servicio.respuesta(servicio.actualizarEstado(id,activo,desde,hasta)));}catch(IllegalArgumentException e){return ResponseEntity.notFound().build();}}
}
