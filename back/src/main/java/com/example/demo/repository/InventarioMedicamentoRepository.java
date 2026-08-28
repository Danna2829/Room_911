package com.example.demo.repository;

import com.example.demo.model.InventarioMedicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventarioMedicamentoRepository extends JpaRepository<InventarioMedicamento, Long> {
    List<InventarioMedicamento> findByIdCategoria(Long idCategoria);
    List<InventarioMedicamento> findByTipoMovimiento(String tipoMovimiento);
}
