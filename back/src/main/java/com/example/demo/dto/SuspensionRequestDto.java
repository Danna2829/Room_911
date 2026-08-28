package com.example.demo.dto;

import java.time.LocalDateTime;

public class SuspensionRequestDto {
    private String idUsuario;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String motivo; // SANCIÓN, INCAPACIDAD, CAMBIO_TURNO

    public SuspensionRequestDto() {}

    public SuspensionRequestDto(String idUsuario, LocalDateTime fechaInicio, LocalDateTime fechaFin, String motivo) {
        this.idUsuario = idUsuario;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.motivo = motivo;
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDateTime fechaFin) { this.fechaFin = fechaFin; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
