package org.example.sala911.servicio;

import org.example.sala911.dto.AccesoSolicitud;
import org.example.sala911.dto.EvaluacionRespuesta;
import org.example.sala911.entidad.*;
import org.example.sala911.repositorio.*;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccesoServicioTest {
    private final UsuarioRepositorio usuarios = mock(UsuarioRepositorio.class);
    private final CronogramaRepositorio cronogramas = mock(CronogramaRepositorio.class);
    private final MedicamentoRepositorio medicamentos = mock(MedicamentoRepositorio.class);
    private final RegistroAccesoRepositorio accesos = mock(RegistroAccesoRepositorio.class);
    private final TareaAlternativaRepositorio tareas = mock(TareaAlternativaRepositorio.class);
    private final AccesoServicio servicio = new AccesoServicio(usuarios, cronogramas, medicamentos, accesos, tareas);

    @Test
    void permiteNivelUnoEnTipoUnoDentroDelCronograma() {
        Medicamento medicamento = medicamento("M001", 1); Usuario usuario = usuario("EMP-301", 1);
        when(usuarios.findById("EMP-301")).thenReturn(Optional.of(usuario)); when(medicamentos.findById("M001")).thenReturn(Optional.of(medicamento)); when(cronogramas.findAll()).thenReturn(List.of(franja("Lunes", medicamento)));
        EvaluacionRespuesta respuesta = servicio.evaluar(new AccesoSolicitud("EMP-301", "ENTRADA", "M001", LocalDateTime.of(2026, 8, 31, 10, 0), "127.0.0.1"));
        assertTrue(respuesta.permitido()); assertEquals("PERMITIDO", respuesta.resultado()); verify(accesos).save(any(RegistroAcceso.class));
    }

    @Test
    void deniegaTipoNoPermitidoYEntregaTareaAlternativa() {
        Medicamento medicamento = medicamento("M003", 4); Usuario usuario = usuario("EMP-301", 1); TareaAlternativa tarea = new TareaAlternativa(); tarea.setNombre("Investigación en Lab-B"); tarea.setDescripcion("Apoyar la revisión documental");
        when(usuarios.findById("EMP-301")).thenReturn(Optional.of(usuario)); when(medicamentos.findById("M003")).thenReturn(Optional.of(medicamento)); when(cronogramas.findAll()).thenReturn(List.of(franja("Lunes", medicamento))); when(tareas.findFirstByActivaTrueOrderByIdAsc()).thenReturn(Optional.of(tarea));
        EvaluacionRespuesta respuesta = servicio.evaluar(new AccesoSolicitud("EMP-301", "ENTRADA", "M003", LocalDateTime.of(2026, 8, 31, 10, 0), null));
        assertFalse(respuesta.permitido()); assertTrue(respuesta.motivo().contains("nivel")); assertTrue(respuesta.tareaAlternativa().startsWith("Investigación en Lab-B"));
    }

    @Test
    void deniegaUsuarioSuspendidoAunqueTengaNivelGlobal() {
        Medicamento medicamento = medicamento("M003", 4); Usuario usuario = usuario("EMP-303", 3); usuario.setActivo(false);
        when(usuarios.findById("EMP-303")).thenReturn(Optional.of(usuario)); when(medicamentos.findById("M003")).thenReturn(Optional.of(medicamento)); when(cronogramas.findAll()).thenReturn(List.of(franja("Lunes", medicamento)));
        EvaluacionRespuesta respuesta = servicio.evaluar(new AccesoSolicitud("EMP-303", "ENTRADA", "M003", LocalDateTime.of(2026, 8, 31, 10, 0), null));
        assertFalse(respuesta.permitido()); assertEquals("El permiso del usuario está suspendido", respuesta.motivo());
    }

    private Usuario usuario(String id, int nivel) { Usuario usuario = new Usuario(); usuario.setIdUsuario(id); usuario.setNombre("Persona"); usuario.setApellido("Demo"); usuario.setRol("OPERARIO"); usuario.setPerfil(new Perfil("Nivel " + nivel, nivel, nivel == 1 ? "1,2" : nivel == 2 ? "2,5" : "1,2,3,4,5")); return usuario; }
    private Medicamento medicamento(String id, int tipo) { return new Medicamento(id, "Medicamento " + id, "Demo", tipo, "Unidad"); }
    private Cronograma franja(String dia, Medicamento medicamento) { Cronograma franja = new Cronograma(); franja.setDiaSemana(dia); franja.setHoraInicio(LocalTime.of(8, 0)); franja.setHoraFin(LocalTime.of(12, 0)); franja.setMedicamento(medicamento); return franja; }
}
