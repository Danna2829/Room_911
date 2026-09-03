package com.example.demo.repository;

import com.example.demo.model.CronogramaOperativo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CronogramaOperativoRepository extends JpaRepository<CronogramaOperativo, Long> {
    Optional<CronogramaOperativo> findByFecha(LocalDate fecha);
    Optional<CronogramaOperativo> findByFechaAndActivoTrue(LocalDate fecha);
    List<CronogramaOperativo> findAllByFechaAndActivoTrue(LocalDate fecha);
    List<CronogramaOperativo> findByActivoTrue();
    List<CronogramaOperativo> findByFechaBetween(LocalDate inicio, LocalDate fin);
}
