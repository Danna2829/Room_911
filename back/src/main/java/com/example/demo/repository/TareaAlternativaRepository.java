package com.example.demo.repository;

import com.example.demo.model.TareaAlternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TareaAlternativaRepository extends JpaRepository<TareaAlternativa, Long> {
    Optional<TareaAlternativa> findByCodigo(String codigo);
    List<TareaAlternativa> findByActivoTrue();
}
