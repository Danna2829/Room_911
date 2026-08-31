package org.example.sala911.controlador;
import org.example.sala911.entidad.Medicamento; import org.example.sala911.repositorio.MedicamentoRepositorio; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/medicamentos") @CrossOrigin(origins="*")
public class MedicamentoControlador { private final MedicamentoRepositorio repo; public MedicamentoControlador(MedicamentoRepositorio r){repo=r;} @GetMapping public Object todos(){return repo.findAll();} @GetMapping("/{id}") public Object uno(@PathVariable String id){return repo.findById(id).orElse(null);} @GetMapping("/tipo/{tipo}") public Object tipo(@PathVariable int tipo){return repo.findByTipo(tipo);} @PostMapping public Medicamento crear(@RequestBody Medicamento m){return repo.save(m);} }
