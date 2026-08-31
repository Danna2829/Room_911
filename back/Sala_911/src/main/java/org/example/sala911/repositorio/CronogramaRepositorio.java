package org.example.sala911.repositorio;
import org.example.sala911.entidad.Cronograma;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface CronogramaRepositorio extends JpaRepository<Cronograma,Long>{ List<Cronograma> findByDiaSemanaIgnoreCase(String diaSemana); }
