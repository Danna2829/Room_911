package org.example.sala911.servicio;

import org.example.sala911.dto.*;
import org.example.sala911.entidad.*;
import org.example.sala911.repositorio.*;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;

@Service
public class AccesoServicio {
    private final UsuarioRepositorio usuarios; private final CronogramaRepositorio cronogramas; private final MedicamentoRepositorio medicamentos;
    private final RegistroAccesoRepositorio accesos; private final TareaAlternativaRepositorio tareas;
    public AccesoServicio(UsuarioRepositorio u,CronogramaRepositorio c,MedicamentoRepositorio m,RegistroAccesoRepositorio a,TareaAlternativaRepositorio t){usuarios=u;cronogramas=c;medicamentos=m;accesos=a;tareas=t;}
    public EvaluacionRespuesta evaluar(AccesoSolicitud solicitud){
        LocalDateTime ahora=solicitud.fechaHora()!=null?solicitud.fechaHora():LocalDateTime.now();
        Usuario usuario=usuarios.findById(solicitud.idUsuario()).orElse(null); String motivo=null; String tarea=null; Medicamento medicamento=null;
        if(usuario==null) motivo="El ID interno no está registrado";
        else if(usuario.estaSuspendido(ahora.toLocalDate())) motivo="El permiso del usuario está suspendido";
        List<Cronograma> activos=cronogramas.findAll().stream().filter(c->mismoDia(c.getDiaSemana(),ahora.getDayOfWeek()) && !ahora.toLocalTime().isBefore(c.getHoraInicio()) && ahora.toLocalTime().isBefore(c.getHoraFin())).toList();
        if(motivo==null && activos.isEmpty()) motivo="No hay medicamento programado para este horario";
        if(solicitud.medicamentoId()!=null && !solicitud.medicamentoId().isBlank()) medicamento=medicamentos.findById(solicitud.medicamentoId()).orElse(null);
        if(motivo==null && medicamento!=null){String medicamentoEvaluado=medicamento.getId(); if(activos.stream().noneMatch(c->c.getMedicamento().getId().equals(medicamentoEvaluado))) motivo="El medicamento no está programado en este horario";}
        if(motivo==null && medicamento==null) medicamento=activos.get(0).getMedicamento();
        if(motivo==null && !usuario.getPerfil().permiteTipo(medicamento.getTipo())) motivo="El nivel del perfil no permite el tipo "+medicamento.getTipo();
        boolean permitido=motivo==null; String resultado=permitido?"PERMITIDO":"DENEGADO";
        TareaAlternativa tareaEntidad=null;
        if(!permitido){tareaEntidad=tareas.findFirstByActivaTrueOrderByIdAsc().orElse(null); tarea=tareaEntidad==null?"Dirigirse a la tarea alternativa asignada":tareaEntidad.getNombre()+": "+tareaEntidad.getDescripcion();}
        {RegistroAcceso registro=new RegistroAcceso(); registro.setUsuario(usuario); registro.setFechaHora(ahora); registro.setAccion(solicitud.accion()==null?"ENTRADA":solicitud.accion().toUpperCase()); registro.setResultado(resultado); registro.setMotivo(motivo); registro.setTareaAlternativa(tarea); registro.setTareaAlternativaEntidad(tareaEntidad); registro.setMedicamentoId(medicamento==null?null:medicamento.getId()); registro.setDireccionIP(solicitud.direccionIP()); accesos.save(registro);}
        return new EvaluacionRespuesta(permitido,resultado,motivo,tarea,usuario==null?solicitud.idUsuario():usuario.getIdUsuario(),medicamento==null?null:medicamento.getNombre(),ahora);
    }
    private boolean mismoDia(String texto,DayOfWeek dia){String a=normalizar(texto);String b=normalizar(dia.getDisplayName(TextStyle.FULL,new Locale("es","CO")));return a.equals(b);}
    private String normalizar(String s){return Normalizer.normalize(s==null?"":s,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT);}
    public List<RegistroAcceso> todos(){return accesos.findAllByOrderByFechaHoraDesc();}
    public List<RegistroAcceso> porUsuario(String id){return accesos.findByUsuarioIdUsuarioOrderByFechaHoraDesc(id);}
    public List<RegistroAcceso> filtrar(String resultado,String idUsuario,LocalDate desde,LocalDate hasta){
        return todos().stream().filter(a -> resultado==null || resultado.isBlank() || resultado.equalsIgnoreCase(a.getResultado()))
                .filter(a -> idUsuario==null || idUsuario.isBlank() || (a.getUsuario()!=null && idUsuario.equalsIgnoreCase(a.getUsuario().getIdUsuario())))
                .filter(a -> desde==null || !a.getFechaHora().toLocalDate().isBefore(desde))
                .filter(a -> hasta==null || !a.getFechaHora().toLocalDate().isAfter(hasta)).toList();
    }
    public String csv(String resultado,String idUsuario,LocalDate desde,LocalDate hasta){
        StringBuilder csv=new StringBuilder("fecha_hora,id_usuario,accion,resultado,medicamento,motivo,tarea_alternativa\n");
        for(RegistroAcceso a:filtrar(resultado,idUsuario,desde,hasta)){
            csv.append(campo(a.getFechaHora())).append(',').append(campo(a.getUsuario()==null?"DESCONOCIDO":a.getUsuario().getIdUsuario())).append(',')
                    .append(campo(a.getAccion())).append(',').append(campo(a.getResultado())).append(',').append(campo(a.getMedicamentoId())).append(',')
                    .append(campo(a.getMotivo())).append(',').append(campo(a.getTareaAlternativa())).append('\n');
        }
        return csv.toString();
    }
    private String campo(Object valor){String texto=valor==null?"":String.valueOf(valor).replace("\"","\"\"");return "\""+texto+"\"";}
}
