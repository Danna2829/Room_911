package org.example.sala911.repositorio;
import org.example.sala911.entidad.Perfil;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface PerfilRepositorio extends JpaRepository<Perfil,Long>{ Optional<Perfil> findByNivel(int nivel); }
