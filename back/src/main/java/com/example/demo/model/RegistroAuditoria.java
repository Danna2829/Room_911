package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "registros_auditoria")
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario; // ID Interno del operario o usuario responsable

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento; // ENTRADA, SALIDA, MODIFICACION_PERMISO, CAMBIO_CRONOGRAMA

    @Column(nullable = false)
    private String resultado; // PERMITIDO, DENEGADO, EXITOSO, FALLIDO

    @Column(name = "motivo_rechazo", columnDefinition = "TEXT")
    private String motivoRechazo;

    @Column(name = "tarea_alternativa_asignada", columnDefinition = "TEXT")
    private String tareaAlternativaAsignada;

    public RegistroAuditoria() {
        this.timestamp = LocalDateTime.now();
    }

    public RegistroAuditoria(String idUsuario, String tipoEvento, String resultado, String motivoRechazo, String tareaAlternativaAsignada) {
        this.idUsuario = idUsuario;
        this.timestamp = LocalDateTime.now();
        this.tipoEvento = tipoEvento;
        this.resultado = resultado;
        this.motivoRechazo = motivoRechazo;
        this.tareaAlternativaAsignada = tareaAlternativaAsignada;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getResultado() { return resultado; }
    public void setResultado(String resultado) { this.resultado = resultado; }

    public String getMotivoRechazo() { return motivoRechazo; }
    public void setMotivoRechazo(String motivoRechazo) { this.motivoRechazo = motivoRechazo; }

    public String getTareaAlternativaAsignada() { return tareaAlternativaAsignada; }
    public void setTareaAlternativaAsignada(String tareaAlternativaAsignada) { this.tareaAlternativaAsignada = tareaAlternativaAsignada; }
}
