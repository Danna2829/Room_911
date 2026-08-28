package com.example.demo.service;

import com.example.demo.model.RegistroAuditoria;
import com.example.demo.repository.RegistroAuditoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReporteService {

    @Autowired
    private RegistroAuditoriaRepository auditoriaRepo;

    public List<RegistroAuditoria> obtenerReporteAccesos() {
        return auditoriaRepo.findAll();
    }

    public List<RegistroAuditoria> obtenerReportePorResultado(String resultado) {
        return auditoriaRepo.findByResultado(resultado);
    }

    public String exportarCSV() {
        List<RegistroAuditoria> logs = auditoriaRepo.findAll();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Expediente,Timestamp,Evento,Resultado,MotivoRechazo,TareaAlternativa\n");

        for (RegistroAuditoria log : logs) {
            csv.append(log.getId()).append(",")
               .append(log.getIdUsuario()).append(",")
               .append(log.getTimestamp()).append(",")
               .append(log.getTipoEvento()).append(",")
               .append(log.getResultado()).append(",")
               .append(log.getMotivoRechazo() != null ? "\"" + log.getMotivoRechazo().replace("\"", "'") + "\"" : "").append(",")
               .append(log.getTareaAlternativaAsignada() != null ? "\"" + log.getTareaAlternativaAsignada().replace("\"", "'") + "\"" : "")
               .append("\n");
        }

        return csv.toString();
    }
}
