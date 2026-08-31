package org.example.sala911.dto;
public record UsuarioRespuesta(String idUsuario,String nombre,String apellido,String correo,String rol,int nivel,String perfil,boolean activo,java.time.LocalDate suspendidoDesde,java.time.LocalDate suspendidoHasta) {}
