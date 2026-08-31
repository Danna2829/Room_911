package org.example.sala911.entidad;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name="acceso")
public class RegistroAcceso {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="usuario_id") private Usuario usuario;
    @Column(name="fecha_hora",nullable=false) private LocalDateTime fechaHora;
    @Column(nullable=false,length=15) private String accion;
    @Column(nullable=false,length=15) private String resultado;
    @Column(length=255) private String motivo;
    @Column(name="tarea_alternativa",length=255) private String tareaAlternativa;
    @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="tarea_alternativa_id") private TareaAlternativa tareaAlternativaEntidad;
    @Column(name="medicamento_id",length=20) private String medicamentoId;
    @Column(name="direccion_ip",length=45) private String direccionIP;
    public RegistroAcceso(){}
    public Long getId(){return id;} public void setId(Long v){id=v;} public Usuario getUsuario(){return usuario;} public void setUsuario(Usuario v){usuario=v;}
    public LocalDateTime getFechaHora(){return fechaHora;} public void setFechaHora(LocalDateTime v){fechaHora=v;} public String getAccion(){return accion;} public void setAccion(String v){accion=v;}
    public String getResultado(){return resultado;} public void setResultado(String v){resultado=v;} public String getMotivo(){return motivo;} public void setMotivo(String v){motivo=v;}
    public String getTareaAlternativa(){return tareaAlternativa;} public void setTareaAlternativa(String v){tareaAlternativa=v;} public String getMedicamentoId(){return medicamentoId;} public void setMedicamentoId(String v){medicamentoId=v;}
    public TareaAlternativa getTareaAlternativaEntidad(){return tareaAlternativaEntidad;} public void setTareaAlternativaEntidad(TareaAlternativa v){tareaAlternativaEntidad=v;}
    public String getDireccionIP(){return direccionIP;} public void setDireccionIP(String v){direccionIP=v;}
}
