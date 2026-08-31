package org.example.sala911.repositorio;
import org.example.sala911.entidad.RegistroAcceso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
public interface RegistroAccesoRepositorio extends JpaRepository<RegistroAcceso,Long>{
    List<RegistroAcceso> findAllByOrderByFechaHoraDesc();
    List<RegistroAcceso> findByUsuarioIdUsuarioOrderByFechaHoraDesc(String id);
    @Query("select a from RegistroAcceso a where (:resultado is null or upper(a.resultado)=upper(:resultado)) and (:idUsuario is null or upper(a.usuario.idUsuario)=upper(:idUsuario)) and (:desde is null or a.fechaHora>=:desde) and (:hasta is null or a.fechaHora<:hasta) order by a.fechaHora desc")
    List<RegistroAcceso> filtrar(@Param("resultado") String resultado,@Param("idUsuario") String idUsuario,@Param("desde") java.time.LocalDateTime desde,@Param("hasta") java.time.LocalDateTime hasta);
}
