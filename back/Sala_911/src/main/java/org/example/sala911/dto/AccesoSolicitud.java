package org.example.sala911.dto;
public record AccesoSolicitud(String idUsuario,String accion,String medicamentoId,java.time.LocalDateTime fechaHora,String direccionIP) {}
