package org.example.sala911.entidad;

import jakarta.persistence.*;
import java.time.LocalTime;

@Entity @Table(name="cronograma")
public class Cronograma {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
    @Column(name="dia_semana",nullable=false,length=15) private String diaSemana;
    @Column(name="hora_inicio",nullable=false) private LocalTime horaInicio;
    @Column(name="hora_fin",nullable=false) private LocalTime horaFin;
    @ManyToOne(fetch=FetchType.EAGER,optional=false) @JoinColumn(name="medicamento_id") private Medicamento medicamento;
    @Column(length=255) private String actividad;
    public Cronograma(){}
    public Long getId(){return id;} public void setId(Long v){id=v;} public String getDiaSemana(){return diaSemana;} public void setDiaSemana(String v){diaSemana=v;}
    public LocalTime getHoraInicio(){return horaInicio;} public void setHoraInicio(LocalTime v){horaInicio=v;} public LocalTime getHoraFin(){return horaFin;} public void setHoraFin(LocalTime v){horaFin=v;}
    public Medicamento getMedicamento(){return medicamento;} public void setMedicamento(Medicamento v){medicamento=v;} public String getActividad(){return actividad;} public void setActividad(String v){actividad=v;}
}
