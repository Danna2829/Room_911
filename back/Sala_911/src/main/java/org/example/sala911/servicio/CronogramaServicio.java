package org.example.sala911.servicio;
import org.example.sala911.dto.CronogramaSolicitud;
import org.example.sala911.entidad.*;
import org.example.sala911.repositorio.*;
import org.springframework.stereotype.Service;
import java.util.List;
@Service public class CronogramaServicio {
    private final CronogramaRepositorio cronogramas; private final MedicamentoRepositorio medicamentos;
    public CronogramaServicio(CronogramaRepositorio c,MedicamentoRepositorio m){cronogramas=c;medicamentos=m;}
    public List<Cronograma> todos(){return cronogramas.findAll();}
    public Cronograma guardar(CronogramaSolicitud s){return guardar(null,s);}
    public Cronograma guardar(Long id,CronogramaSolicitud s){Cronograma c=id==null?new Cronograma():cronogramas.findById(id).orElseThrow(()->new IllegalArgumentException("Cronograma no encontrado"));c.setDiaSemana(s.diaSemana());c.setHoraInicio(s.horaInicio());c.setHoraFin(s.horaFin());c.setActividad(s.actividad());c.setMedicamento(medicamentos.findById(s.medicamentoId()).orElseThrow(()->new IllegalArgumentException("Medicamento no encontrado")));return cronogramas.save(c);}
    public void eliminar(Long id){cronogramas.deleteById(id);}
}
