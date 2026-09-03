package com.example.demo.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Perfil de acceso de un operario para el torniquete:
 * nivel ABAC, categorias que puede manipular y programacion del dia.
 */
public class PerfilAccesoDto {

    private String idUsuario;
    private String nombre;
    private String rol;
    private Integer nivelAcceso;
    private String descripcionPerfil;
    private Boolean estado;

    /** Categorias programadas hoy en room_911 con el veredicto ABAC por categoria. */
    private List<Map<String, Object>> cronogramaHoy = new ArrayList<>();

    /** Categorias de medicamento que el nivel del operario puede manipular. */
    private List<Map<String, Object>> categoriasPermitidas = new ArrayList<>();

    public String getIdUsuario() { return idUsuario; }
    public void setIdUsuario(String idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }

    public Integer getNivelAcceso() { return nivelAcceso; }
    public void setNivelAcceso(Integer nivelAcceso) { this.nivelAcceso = nivelAcceso; }

    public String getDescripcionPerfil() { return descripcionPerfil; }
    public void setDescripcionPerfil(String descripcionPerfil) { this.descripcionPerfil = descripcionPerfil; }

    public Boolean getEstado() { return estado; }
    public void setEstado(Boolean estado) { this.estado = estado; }

    public List<Map<String, Object>> getCronogramaHoy() { return cronogramaHoy; }
    public void setCronogramaHoy(List<Map<String, Object>> cronogramaHoy) { this.cronogramaHoy = cronogramaHoy; }

    public List<Map<String, Object>> getCategoriasPermitidas() { return categoriasPermitidas; }
    public void setCategoriasPermitidas(List<Map<String, Object>> categoriasPermitidas) { this.categoriasPermitidas = categoriasPermitidas; }
}
