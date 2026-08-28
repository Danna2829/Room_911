package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tareas_alternativas")
public class TareaAlternativa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String descripcion; // ej. "Asignado a investigación en Lab-B", "Atención a clientes"

    @Column(name = "activo")
    private Boolean activo = true;

    public TareaAlternativa() {
        this.activo = true;
    }

    public TareaAlternativa(String codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
        this.activo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getActivo() { return activo != null ? activo : true; }
    public void setActivo(Boolean activo) { this.activo = activo != null ? activo : true; }
}
