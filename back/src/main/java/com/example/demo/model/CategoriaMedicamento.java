package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "categorias_medicamento")
public class CategoriaMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo; // ej. TIPO_1, TIPO_2, TIPO_3, TIPO_4, TIPO_5

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "es_restringido")
    private Boolean esRestringido = false;

    @Column(name = "activo")
    private Boolean activo = true;

    public CategoriaMedicamento() {}

    public CategoriaMedicamento(String codigo, String nombre, String descripcion, Boolean esRestringido) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.esRestringido = esRestringido != null ? esRestringido : false;
        this.activo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Boolean getEsRestringido() { return esRestringido; }
    public void setEsRestringido(Boolean esRestringido) { this.esRestringido = esRestringido; }

    public Boolean getActivo() { return activo != null ? activo : true; }
    public void setActivo(Boolean activo) { this.activo = activo != null ? activo : true; }
}
