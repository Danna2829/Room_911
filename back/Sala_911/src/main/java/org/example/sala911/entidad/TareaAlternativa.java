package org.example.sala911.entidad;

import jakarta.persistence.*;

@Entity @Table(name="tarea_alternativa")
public class TareaAlternativa {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,length=120) private String nombre;
    @Column(nullable=false,length=255) private String descripcion;
    @Column(nullable=false) private boolean activa=true;
    public TareaAlternativa(){}
    public Long getId(){return id;} public void setId(Long v){id=v;} public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
    public String getDescripcion(){return descripcion;} public void setDescripcion(String v){descripcion=v;} public boolean isActiva(){return activa;} public void setActiva(boolean v){activa=v;}
}
