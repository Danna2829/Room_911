package org.example.sala911.repositorio;
import org.example.sala911.entidad.TareaAlternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface TareaAlternativaRepositorio extends JpaRepository<TareaAlternativa,Long>{ Optional<TareaAlternativa> findFirstByActivaTrueOrderByIdAsc(); }
