package com.example.demo.repository;

import com.example.demo.model.EventoEspecial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoEspecialRepository extends JpaRepository<EventoEspecial, Long> {
}
