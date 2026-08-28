package com.example.demo.dto;

import java.time.LocalDate;

public class CronogramaRequestDto {
    private LocalDate fecha;
    private Long idCategoria;
    private String observaciones;

    public CronogramaRequestDto() {}

    public CronogramaRequestDto(LocalDate fecha, Long idCategoria, String observaciones) {
        this.fecha = fecha;
        this.idCategoria = idCategoria;
        this.observaciones = observaciones;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
