package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @Column(name = "id_usuario")
    private String idUsuario; // Formato de expediente interno ej: EMP-8821

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String correo;

    private String rol; // ADMINISTRADOR, GUARDIA_SEGURIDAD, OPERARIO, SECRETARIA

    @Column(name = "id_rol")
    private Integer idRol; // Vinculo con el catalogo roles (sincronizado por UsuarioService)

    @Column(name = "contrasena", nullable = false)
    @JsonProperty("contraseña")
    @JsonAlias({"contrasena", "contraseña"})
    private String contrasena;

    private Boolean estado = true; // Valor por defecto para evitar null

    @Column(name = "token_reset")
    private String tokenReset;

    @Column(name = "token_expiracion")
    private LocalDateTime tokenExpiracion;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Constructor vacío (necesario para JPA)
    public Usuario() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
    }

    // Constructor con parámetros
    public Usuario(String idUsuario, String nombre, String apellido, String correo,
                   String rol, String contrasena, Boolean estado,
                   LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
        this.correo = correo;
        this.rol = rol;
        this.contrasena = contrasena;
        this.estado = (estado != null) ? estado : true;
        this.fechaCreacion = (fechaCreacion != null) ? fechaCreacion : LocalDateTime.now();
        this.fechaActualizacion = (fechaActualizacion != null) ? fechaActualizacion : LocalDateTime.now();
    }

    // Getters y Setters
    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Integer getIdRol() { return idRol; }
    public void setIdRol(Integer idRol) { this.idRol = idRol; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public Boolean getEstado() { return estado != null ? estado : true; }
    public Boolean isEstado() { return getEstado(); }
    public void setEstado(Boolean estado) { this.estado = (estado != null) ? estado : true; }

    public String getTokenReset() { return tokenReset; }
    public void setTokenReset(String tokenReset) { this.tokenReset = tokenReset; }

    public LocalDateTime getTokenExpiracion() { return tokenExpiracion; }
    public void setTokenExpiracion(LocalDateTime tokenExpiracion) { this.tokenExpiracion = tokenExpiracion; }

    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public LocalDateTime getFechaActualizacion() { return fechaActualizacion; }
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) { this.fechaActualizacion = fechaActualizacion; }
}
