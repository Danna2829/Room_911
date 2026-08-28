package com.example.demo.repository;

import com.example.demo.model.CategoriaMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaMedicamentoRepository extends JpaRepository<CategoriaMedicamento, Long> {
    Optional<CategoriaMedicamento> findByCodigo(String codigo);
}
