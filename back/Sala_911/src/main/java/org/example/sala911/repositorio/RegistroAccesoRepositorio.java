package org.example.sala911.repositorio;
import org.example.sala911.entidad.RegistroAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface RegistroAccesoRepositorio extends JpaRepository<RegistroAcceso,Long>{ List<RegistroAcceso> findAllByOrderByFechaHoraDesc(); List<RegistroAcceso> findByUsuarioIdUsuarioOrderByFechaHoraDesc(String id); }
