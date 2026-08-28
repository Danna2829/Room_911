package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "cronograma_operativo")
public class CronogramaOperativo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria; // ID de la categoría de medicamento programada para el día en room_911

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "activo")
    private Boolean activo = true;

    public CronogramaOperativo() {}

    public CronogramaOperativo(LocalDate fecha, Long idCategoria, String observaciones) {
        this.fecha = fecha;
        this.idCategoria = idCategoria;
        this.observaciones = observaciones;
        this.activo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public Boolean getActivo() { return activo != null ? activo : true; }
    public void setActivo(Boolean activo) { this.activo = activo != null ? activo : true; }
}
