package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Bitacora de eventos especiales registrados por la Guardia
 * (incidentes, alertas de seguridad u ocurrencias distintas al intento de acceso).
 */
@Entity
@Table(name = "eventos_especiales")
public class EventoEspecial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Long idEvento;

    @Column(name = "id_usuario")
    private String idUsuario;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    private String descripcion;

    @Column(name = "fecha_hora")
    private LocalDateTime fechaHora;

    public EventoEspecial() {
        this.fechaHora = LocalDateTime.now();
    }

    public EventoEspecial(String idUsuario, String tipoEvento, String descripcion) {
        this.idUsuario = idUsuario;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.fechaHora = LocalDateTime.now();
    }

    public Long getIdEvento() { return idEvento; }
    public void setIdEvento(Long idEvento) { this.idEvento = idEvento; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(String tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}
