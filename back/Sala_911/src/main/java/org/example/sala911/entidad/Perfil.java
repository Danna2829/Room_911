package org.example.sala911.entidad;

import jakarta.persistence.*;

@Entity @Table(name="perfil")
public class Perfil {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(nullable=false,unique=true,length=40) private String nombre;
    @Column(nullable=false) private int nivel;
    @Column(name="tipos_permitidos",nullable=false,length=100) private String tiposPermitidos;
    public Perfil(){}
    public Perfil(String nombre,int nivel,String tiposPermitidos){this.nombre=nombre;this.nivel=nivel;this.tiposPermitidos=tiposPermitidos;}
    public Long getId(){return id;} public void setId(Long v){id=v;}
    public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
    public int getNivel(){return nivel;} public void setNivel(int v){nivel=v;}
    public String getTiposPermitidos(){return tiposPermitidos;} public void setTiposPermitidos(String v){tiposPermitidos=v;}
    public boolean permiteTipo(int tipo){return nivel==3 || java.util.Arrays.stream(tiposPermitidos.split(",")).anyMatch(t->t.trim().equals(String.valueOf(tipo)));}
}
