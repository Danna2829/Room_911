package com.example.demo.repository;

import com.example.demo.model.PerfilOperario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PerfilOperarioRepository extends JpaRepository<PerfilOperario, Long> {
    Optional<PerfilOperario> findByIdUsuario(String idUsuario);
}
