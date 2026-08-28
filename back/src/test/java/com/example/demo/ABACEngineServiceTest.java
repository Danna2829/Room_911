package com.example.demo;

import com.example.demo.dto.AccesoRequestDto;
import com.example.demo.dto.AccesoResponseDto;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.ABACEngineService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ABACEngineServiceTest {

    @InjectMocks
    private ABACEngineService abacEngineService;

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private PerfilOperarioRepository perfilRepo;

    @Mock
    private CategoriaMedicamentoRepository categoriaRepo;

    @Mock
    private CronogramaOperativoRepository cronogramaRepo;

    @Mock
    private SuspensionPermisoRepository suspensionRepo;

    @Mock
    private TareaAlternativaRepository tareaRepo;

    @Mock
    private RegistroAuditoriaRepository auditoriaRepo;

    private Usuario usuarioNivel1;
    private Usuario usuarioNivel2;
    private Usuario usuarioNivel3;

    @BeforeEach
    void setUp() {
        usuarioNivel1 = new Usuario("EMP-8821", "Juan", "Perez", "juan@lab.com", "OPERARIO", "pass", true, LocalDateTime.now(), LocalDateTime.now());
        usuarioNivel2 = new Usuario("EMP-8822", "Maria", "Lopez", "maria@lab.com", "OPERARIO", "pass", true, LocalDateTime.now(), LocalDateTime.now());
        usuarioNivel3 = new Usuario("EMP-8823", "Pedro", "Gomez", "pedro@lab.com", "OPERARIO", "pass", true, LocalDateTime.now(), LocalDateTime.now());
    }

    @Test
    @DisplayName("Evaluar Matriz de Permisos ABAC: Nivel 1, 2 y 3")
    void testEvaluarMatrizPermisos() {
        assertTrue(abacEngineService.evaluarMatrizPermisos(1, "TIPO_1"));
        assertTrue(abacEngineService.evaluarMatrizPermisos(1, "TIPO_2"));
        assertFalse(abacEngineService.evaluarMatrizPermisos(1, "TIPO_4"));

        assertFalse(abacEngineService.evaluarMatrizPermisos(2, "TIPO_1"));
        assertTrue(abacEngineService.evaluarMatrizPermisos(2, "TIPO_2"));
        assertTrue(abacEngineService.evaluarMatrizPermisos(2, "TIPO_5"));
        assertFalse(abacEngineService.evaluarMatrizPermisos(2, "TIPO_4"));

        assertTrue(abacEngineService.evaluarMatrizPermisos(3, "TIPO_1"));
        assertTrue(abacEngineService.evaluarMatrizPermisos(3, "TIPO_4")); // Acceso Global Tipo 4 Restringido
    }

    @Test
    @DisplayName("Caso Borde 1: Operario Nivel 1 Denegado el día de Medicamento Tipo 4 con Redirección a Lab-B")
    void testAccesoDenegadoPorCronograma() {
        when(usuarioRepo.findById("EMP-8821")).thenReturn(Optional.of(usuarioNivel1));
        when(suspensionRepo.findByIdUsuarioAndActivoTrue("EMP-8821")).thenReturn(Collections.emptyList());

        CronogramaOperativo cronogramaHoy = new CronogramaOperativo(LocalDate.now(), 4L, "Procesamiento Tipo 4");
        CategoriaMedicamento cat4 = new CategoriaMedicamento("TIPO_4", "Medicamento Tipo 4 Restringido", "Alto Control", true);

        when(cronogramaRepo.findByFecha(LocalDate.now())).thenReturn(Optional.of(cronogramaHoy));
        when(categoriaRepo.findById(4L)).thenReturn(Optional.of(cat4));
        when(perfilRepo.findByIdUsuario("EMP-8821")).thenReturn(Optional.of(new PerfilOperario("EMP-8821", 1, "Operario Nivel 1")));

        TareaAlternativa tareaLabB = new TareaAlternativa("TASK_LAB_B", "Acceso denegado: Asignado a investigación en Lab-B");
        when(tareaRepo.findByActivoTrue()).thenReturn(List.of(tareaLabB));

        AccesoRequestDto request = new AccesoRequestDto("EMP-8821", "ENTRADA");
        AccesoResponseDto response = abacEngineService.evaluarAcceso(request);

        assertFalse(response.isPermitido());
        assertEquals("DENEGADO", response.getResultado());
        assertNotNull(response.getTareaAlternativa());
        assertTrue(response.getTareaAlternativa().contains("Lab-B"));
        verify(auditoriaRepo, times(1)).save(any());
    }

    @Test
    @DisplayName("Caso Borde 2: Operario Nivel 2 ingresando el día de Medicamento Tipo 5 (Permitido)")
    void testAccesoPermitidoNivel2Tipo5() {
        when(usuarioRepo.findById("EMP-8822")).thenReturn(Optional.of(usuarioNivel2));
        when(suspensionRepo.findByIdUsuarioAndActivoTrue("EMP-8822")).thenReturn(Collections.emptyList());

        CronogramaOperativo cronogramaHoy = new CronogramaOperativo(LocalDate.now(), 5L, "Procesamiento Tipo 5");
        CategoriaMedicamento cat5 = new CategoriaMedicamento("TIPO_5", "Medicamento Tipo 5", "Control Especial", false);

        when(cronogramaRepo.findByFecha(LocalDate.now())).thenReturn(Optional.of(cronogramaHoy));
        when(categoriaRepo.findById(5L)).thenReturn(Optional.of(cat5));
        when(perfilRepo.findByIdUsuario("EMP-8822")).thenReturn(Optional.of(new PerfilOperario("EMP-8822", 2, "Operario Nivel 2")));

        AccesoRequestDto request = new AccesoRequestDto("EMP-8822", "ENTRADA");
        AccesoResponseDto response = abacEngineService.evaluarAcceso(request);

        assertTrue(response.isPermitido());
        assertEquals("PERMITIDO", response.getResultado());
    }

    @Test
    @DisplayName("Caso Borde 3: Marcación de SALIDA siempre permitida para usuarios activos")
    void testMarcacionSalidaPermitida() {
        when(usuarioRepo.findById("EMP-8821")).thenReturn(Optional.of(usuarioNivel1));
        when(suspensionRepo.findByIdUsuarioAndActivoTrue("EMP-8821")).thenReturn(Collections.emptyList());

        AccesoRequestDto request = new AccesoRequestDto("EMP-8821", "SALIDA");
        AccesoResponseDto response = abacEngineService.evaluarAcceso(request);

        assertTrue(response.isPermitido());
        assertEquals("PERMITIDO", response.getResultado());
        assertEquals("Salida registrada correctamente", response.getMensaje());
    }

    @Test
    @DisplayName("Caso Borde 4: Usuario inactivo bloqueado al intentar ingresar")
    void testUsuarioInactivoDenegado() {
        Usuario usuarioInactivo = new Usuario("EMP-9999", "Inactivo", "User", "inactivo@lab.com", "OPERARIO", "pass", false, LocalDateTime.now(), LocalDateTime.now());
        when(usuarioRepo.findById("EMP-9999")).thenReturn(Optional.of(usuarioInactivo));

        AccesoRequestDto request = new AccesoRequestDto("EMP-9999", "ENTRADA");
        AccesoResponseDto response = abacEngineService.evaluarAcceso(request);

        assertFalse(response.isPermitido());
        assertEquals("DENEGADO", response.getResultado());
        assertTrue(response.getMotivoRechazo().contains("inactivo"));
    }

    @Test
    @DisplayName("Caso Borde 5: Acceso Denegado por Suspensión Activa de Guardia")
    void testAccesoDenegadoPorSuspension() {
        when(usuarioRepo.findById("EMP-8821")).thenReturn(Optional.of(usuarioNivel1));

        SuspensionPermiso suspension = new SuspensionPermiso("EMP-8821", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(2), "INCAPACIDAD MEDICA", true);
        when(suspensionRepo.findByIdUsuarioAndActivoTrue("EMP-8821")).thenReturn(List.of(suspension));

        AccesoRequestDto request = new AccesoRequestDto("EMP-8821", "ENTRADA");
        AccesoResponseDto response = abacEngineService.evaluarAcceso(request);

        assertFalse(response.isPermitido());
        assertEquals("DENEGADO", response.getResultado());
        assertTrue(response.getMotivoRechazo().contains("INCAPACIDAD MEDICA"));
    }
}
