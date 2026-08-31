package org.example.sala911.repositorio;
import org.example.sala911.entidad.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UsuarioRepositorio extends JpaRepository<Usuario,String> {}
