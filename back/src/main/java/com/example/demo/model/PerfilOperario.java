package com.example.demo.model;

import jakarta.persistence.*;

@Entity
@Table(name = "perfiles_operario")
public class PerfilOperario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_usuario", nullable = false)
    private String idUsuario; // Relacionado con Usuario.idUsuario (ej. EMP-8821)

    @Column(name = "nivel_acceso", nullable = false)
    private Integer nivelAcceso; // 1: Tipo 1 y 2; 2: Tipo 2 y 5; 3: Global (Todos + Tipo 4 Especial)

    private String descripcion;

    public PerfilOperario() {}

    public PerfilOperario(String idUsuario, Integer nivelAcceso, String descripcion) {
        this.idUsuario = idUsuario;
        this.nivelAcceso = nivelAcceso;
        this.descripcion = descripcion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public Integer getNivelAcceso() { return nivelAcceso; }
    public void setNivelAcceso(Integer nivelAcceso) { this.nivelAcceso = nivelAcceso; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
