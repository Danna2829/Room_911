package com.example.demo.dto;

public class AccesoRequestDto {
    private String idUsuario;
    private String tipoEvento; // ENTRADA o SALIDA

    public AccesoRequestDto() {}

    public AccesoRequestDto(String idUsuario, String tipoEvento) {
        this.idUsuario = idUsuario;
        this.tipoEvento = tipoEvento;
    }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }
}
