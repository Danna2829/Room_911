package com.example.demo.controller;

import com.example.demo.dto.DashboardResumenDto;
import com.example.demo.repository.CategoriaMedicamentoRepository;
import com.example.demo.repository.CronogramaOperativoRepository;
import com.example.demo.repository.RegistroAuditoriaRepository;
import com.example.demo.repository.SuspensionPermisoRepository;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Metricas operativas del Panel General (solo datos reales del sistema).
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private RegistroAuditoriaRepository auditoriaRepo;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private CategoriaMedicamentoRepository categoriaRepo;

    @Autowired
    private CronogramaOperativoRepository cronogramaRepo;

    @Autowired
    private SuspensionPermisoRepository suspensionRepo;

    @GetMapping("/resumen")
    public ResponseEntity<DashboardResumenDto> resumen() {
        DashboardResumenDto dto = new DashboardResumenDto();

        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDate.now().plusDays(1).atStartOfDay();

        var accesosHoy = auditoriaRepo.findByTimestampBetween(inicioDia, finDia);
        dto.setAccesosHoy(accesosHoy.size());
        dto.setPermitidosHoy(accesosHoy.stream().filter(r -> "PERMITIDO".equals(r.getResultado())).count());
        dto.setDenegadosHoy(accesosHoy.size() - dto.getPermitidosHoy());

        dto.setOperariosActivos(usuarioRepo.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getEstado()) && "OPERARIO".equalsIgnoreCase(u.getRol()))
                .count());

        dto.setMedicamentosActivos(categoriaRepo.findAll().stream()
                .filter(c -> Boolean.TRUE.equals(c.getActivo()))
                .count());

        dto.setProgramadosHoy(cronogramaRepo.findAllByFechaAndActivoTrue(LocalDate.now()).size());

        dto.setSuspensionesVigentes(suspensionRepo.findAll().stream()
                .filter(s -> Boolean.TRUE.equals(s.getActivo()))
                .count());

        dto.setUltimosAccesos(auditoriaRepo.findTop10ByOrderByTimestampDesc());

        return ResponseEntity.ok(dto);
    }
}
