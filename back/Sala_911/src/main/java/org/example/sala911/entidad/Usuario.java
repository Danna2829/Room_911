package org.example.sala911.entidad;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity @Table(name = "usuario")
public class Usuario {
    @Id @Column(name = "id_usuario", length = 30) private String idUsuario;
    @Column(nullable = false, length = 80) private String nombre;
    @Column(nullable = false, length = 80) private String apellido;
    @Column(length = 160) private String correo;
    @Column(nullable = false, length = 20) private String rol = "OPERARIO";
    @ManyToOne(fetch = FetchType.EAGER, optional = false) @JoinColumn(name = "perfil_id", nullable = false) private Perfil perfil;
    @Column(nullable = false) private boolean activo = true;
    private LocalDate suspendidoDesde;
    private LocalDate suspendidoHasta;
    public Usuario() {}
    public Usuario(String idUsuario, String nombre, String apellido, String correo, Perfil perfil) { this.idUsuario=idUsuario; this.nombre=nombre; this.apellido=apellido; this.correo=correo; this.perfil=perfil; }
    public String getIdUsuario(){return idUsuario;} public void setIdUsuario(String v){idUsuario=v;}
    public String getNombre(){return nombre;} public void setNombre(String v){nombre=v;}
    public String getApellido(){return apellido;} public void setApellido(String v){apellido=v;}
    public String getCorreo(){return correo;} public void setCorreo(String v){correo=v;}
    public String getRol(){return rol;} public void setRol(String v){rol=v;}
    public Perfil getPerfil(){return perfil;} public void setPerfil(Perfil v){perfil=v;}
    public boolean isActivo(){return activo;} public void setActivo(boolean v){activo=v;}
    public LocalDate getSuspendidoDesde(){return suspendidoDesde;} public void setSuspendidoDesde(LocalDate v){suspendidoDesde=v;}
    public LocalDate getSuspendidoHasta(){return suspendidoHasta;} public void setSuspendidoHasta(LocalDate v){suspendidoHasta=v;}
    public boolean estaSuspendido(LocalDate fecha){return !activo || (suspendidoDesde != null && !fecha.isBefore(suspendidoDesde) && (suspendidoHasta == null || !fecha.isAfter(suspendidoHasta)));}
}
