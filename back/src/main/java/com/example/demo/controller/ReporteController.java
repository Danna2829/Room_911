package com.example.demo.controller;

import com.example.demo.model.RegistroAuditoria;
import com.example.demo.service.ReporteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    @Autowired
    private ReporteService reporteService;

    @GetMapping("/accesos")
    public ResponseEntity<List<RegistroAuditoria>> obtenerReporteAccesos() {
        return ResponseEntity.ok(reporteService.obtenerReporteAccesos());
    }

    @GetMapping("/exportar/csv")
    public ResponseEntity<String> exportarCSV() {
        String csvData = reporteService.exportarCSV();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_room911.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvData);
    }

    @GetMapping("/exportar/xlsx")
    public ResponseEntity<byte[]> exportarXLSX() throws java.io.IOException {
        byte[] archivo = reporteService.exportarXLSX();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_room911.xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(archivo);
    }

    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPDF() throws java.io.IOException {
        byte[] archivo = reporteService.exportarPDF();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_room911.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(archivo);
    }
}
