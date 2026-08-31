package org.example.sala911.repositorio;
import org.example.sala911.entidad.Medicamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MedicamentoRepositorio extends JpaRepository<Medicamento,String>{ List<Medicamento> findByTipo(int tipo); }
