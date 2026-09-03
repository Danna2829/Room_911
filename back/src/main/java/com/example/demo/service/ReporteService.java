package com.example.demo.service;

import com.example.demo.model.RegistroAuditoria;
import com.example.demo.repository.RegistroAuditoriaRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteService {

    private static final String[] COLUMNAS = {
        "ID", "Expediente", "Fecha/Hora", "Evento", "Resultado", "Motivo rechazo", "Tarea alternativa"
    };

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    /**
     * Exporta el reporte de accesos a Excel (XLSX) con encabezado estilizado.
     */
    public byte[] exportarXLSX() throws IOException {
        List<RegistroAuditoria> logs = auditoriaRepo.findAll();
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Accesos room_911");

            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

            Row header = sheet.createRow(0);
            for (int i = 0; i < COLUMNAS.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(COLUMNAS[i]);
                c.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, i == 0 ? 2000 : 7000);
            }

            int rowIdx = 1;
            for (RegistroAuditoria log : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getId() != null ? log.getId() : 0);
                row.createCell(1).setCellValue(nvl(log.getIdUsuario()));
                row.createCell(2).setCellValue(log.getTimestamp() != null ? log.getTimestamp().format(FECHA) : "");
                row.createCell(3).setCellValue(nvl(log.getTipoEvento()));
                row.createCell(4).setCellValue(nvl(log.getResultado()));
                row.createCell(5).setCellValue(nvl(log.getMotivoRechazo()));
                row.createCell(6).setCellValue(nvl(log.getTareaAlternativaAsignada()));
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            return out.toByteArray();
        }
    }

    /**
     * Exporta el reporte de accesos a PDF (tabla con encabezado y veredicto coloreado).
     */
    public byte[] exportarPDF() throws IOException {
        List<RegistroAuditoria> logs = auditoriaRepo.findAll();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, out);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            titleFont.setColor(new Color(15, 40, 80));
            Paragraph titulo = new Paragraph("room_911 - Reporte de Accesos", titleFont);
            titulo.setSpacingAfter(6);
            document.add(titulo);

            Font subFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            subFont.setColor(new Color(90, 90, 90));
            Paragraph sub = new Paragraph("Generado: " + java.time.LocalDateTime.now().format(FECHA) + " - Registros: " + logs.size(), subFont);
            sub.setSpacingAfter(14);
            document.add(sub);

            PdfPTable table = new PdfPTable(COLUMNAS.length);
            table.setWidthPercentage(100);
            table.setWidths(new float[] {1f, 2f, 2.6f, 1.6f, 2f, 4f, 4f});

            Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9);
            headFont.setColor(Color.WHITE);
            for (String col : COLUMNAS) {
                com.lowagie.text.pdf.PdfPCell cell = new com.lowagie.text.pdf.PdfPCell(new Paragraph(col, headFont));
                cell.setBackgroundColor(new Color(15, 40, 80));
                cell.setPadding(5);
                table.addCell(cell);
            }

            Font bodyFont = FontFactory.getFont(FontFactory.HELVETICA, 8);
            for (RegistroAuditoria log : logs) {
                table.addCell(new Paragraph(String.valueOf(log.getId() != null ? log.getId() : ""), bodyFont));
                table.addCell(new Paragraph(nvl(log.getIdUsuario()), bodyFont));
                table.addCell(new Paragraph(log.getTimestamp() != null ? log.getTimestamp().format(FECHA) : "", bodyFont));
                table.addCell(new Paragraph(nvl(log.getTipoEvento()), bodyFont));

                boolean permitido = "PERMITIDO".equals(log.getResultado());
                Font resFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8,
                        permitido ? new Color(21, 128, 61) : new Color(185, 28, 28));
                table.addCell(new Paragraph(nvl(log.getResultado()), resFont));
                table.addCell(new Paragraph(nvl(log.getMotivoRechazo()), bodyFont));
                table.addCell(new Paragraph(nvl(log.getTareaAlternativaAsignada()), bodyFont));
            }

            document.add(table);
            document.close();
            return out.toByteArray();
        }
    }

    private String nvl(String s) {
        return s != null ? s : "";
    }
}
