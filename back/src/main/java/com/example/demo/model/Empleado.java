package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Empleado: persona / expediente interno (EMP-XXXX) de la empresa.
 * Separado de la cuenta de acceso (usuarios): un empleado puede existir sin
 * cuenta web y su estado operativo (ACTIVO/SUSPENDIDO) es gestionado por
 * la Guardia y la administracion.
 */
@Entity
@Table(name = "empleados")
public class Empleado {

    @Id
    @Column(name = "id_empleado")
    private String idEmpleado;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    /** Nivel ABAC del operario (1 a 3); null para roles administrativos. */
    private Integer nivel;

    /** ACTIVO o SUSPENDIDO. */
    private String estado;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    public Empleado() {
        this.estado = "ACTIVO";
        this.fechaCreacion = LocalDateTime.now();
    }

    public Empleado(String idEmpleado, String nombre, String apellido, Integer nivel) {
        this.idEmpleado = idEmpleado;
        this.nombre = nombre;
        this.apellido = apellido;
        this.nivel = nivel;
        this.estado = "ACTIVO";
        this.fechaCreacion = LocalDateTime.now();
    }

    public String getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(String idEmpleado) { this.idEmpleado = idEmpleado; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public Integer getNivel() { return nivel; }
    public void setNivel(Integer nivel) { this.nivel = nivel; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
