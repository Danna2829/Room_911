package org.example.sala911.entidad;

import jakarta.persistence.*;

@Entity @Table(name="medicamento")
public class Medicamento {
    @Id @Column(length=20) private String id;
    @Column(nullable=false,length=100) private String nombre;
    @Column(length=255) private String descripcion;
    @Column(nullable=false) private int tipo;
    @Column(name="unidad_medida",length=80) private String unidadMedida;
    public Medicamento(){}
    public Medicamento(String id,String nombre,String descripcion,int tipo,String unidadMedida){this.id=id;this.nombre=nombre;this.descripcion=descripcion;this.tipo=tipo;this.unidadMedida=unidadMedida;}
    public String getId(){return id;} public void setId(String v){id=v;} public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
    public String getDescripcion(){return descripcion;} public void setDescripcion(String v){descripcion=v;} public int getTipo(){return tipo;} public void setTipo(int v){tipo=v;}
    public String getUnidadMedida(){return unidadMedida;} public void setUnidadMedida(String v){unidadMedida=v;}
}
