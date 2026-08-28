package com.example.demo.repository;

import com.example.demo.model.SuspensionPermiso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SuspensionPermisoRepository extends JpaRepository<SuspensionPermiso, Long> {
    List<SuspensionPermiso> findByIdUsuarioAndActivoTrue(String idUsuario);
    List<SuspensionPermiso> findByIdUsuarioAndActivoTrueAndFechaInicioBeforeAndFechaFinAfter(String idUsuario, LocalDateTime inicio, LocalDateTime fin);
}
