package com.example.demo.repository;

import com.example.demo.model.RegistroAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RegistroAuditoriaRepository extends JpaRepository<RegistroAuditoria, Long> {
    List<RegistroAuditoria> findByIdUsuario(String idUsuario);
    List<RegistroAuditoria> findByTimestampBetween(LocalDateTime inicio, LocalDateTime fin);
    List<RegistroAuditoria> findByResultado(String resultado);
}
