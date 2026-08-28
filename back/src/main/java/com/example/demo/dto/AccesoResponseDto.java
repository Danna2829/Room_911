package com.example.demo.dto;

import java.time.LocalDateTime;

public class AccesoResponseDto {
    private boolean permitido;
    private String resultado; // PERMITIDO o DENEGADO
    private String mensaje;
    private String motivoRechazo;
    private String tareaAlternativa;
    private String idUsuario;
    private String timestamp;

    public AccesoResponseDto() {
        this.timestamp = LocalDateTime.now().toString();
    }

    public AccesoResponseDto(boolean permitido, String resultado, String mensaje, String motivoRechazo, String tareaAlternativa, String idUsuario) {
        this.permitido = permitido;
        this.resultado = resultado;
        this.mensaje = mensaje;
        this.motivoRechazo = motivoRechazo;
        this.tareaAlternativa = tareaAlternativa;
        this.idUsuario = idUsuario;
        this.timestamp = LocalDateTime.now().toString();
    }

    public boolean isPermitido() { return permitido; }
    public void setPermitido(boolean permitido) { this.permitido = permitido; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public String getTareaAlternativa() { return tareaAlternativa; }
    public void setTareaAlternativa(String tareaAlternativa) { this.tareaAlternativa = tareaAlternativa; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
