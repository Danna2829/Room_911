package com.example.demo.service;

import com.example.demo.dto.AccesoRequestDto;
import com.example.demo.dto.AccesoResponseDto;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ABACEngineService {

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private PerfilOperarioRepository perfilRepo;

    @Autowired
    private CategoriaMedicamentoRepository categoriaRepo;

    @Autowired
    private CronogramaOperativoRepository cronogramaRepo;

    @Autowired
    private SuspensionPermisoRepository suspensionRepo;

    @Autowired
    private TareaAlternativaRepository tareaRepo;

    @Autowired
    private RegistroAuditoriaRepository auditoriaRepo;

    /**
     * Evalúa dinámicamente si un empleado tiene permitido el acceso a room_911
     * según la Matriz de Control de Acceso Basada en Atributos (ABAC):
     * (Empleado + Medicamento Programado del Día + Estado de Guardia)
     */
    public AccesoResponseDto evaluarAcceso(AccesoRequestDto request) {
        String idUsuario = request.getIdUsuario() != null ? request.getIdUsuario().trim() : "";
        String tipoEvento = request.getTipoEvento() != null ? request.getTipoEvento().trim().toUpperCase() : "ENTRADA";
        LocalDateTime now = LocalDateTime.now();

        // 1. Validar existencia de Usuario
        Optional<Usuario> optUsuario = usuarioRepo.findById(idUsuario);
        if (optUsuario.isEmpty()) {
            return registrarYResponder(idUsuario, tipoEvento, false, "DENEGADO", 
                    "Usuario no encontrado", "El expediente " + idUsuario + " no existe en el sistema", null);
        }

        Usuario usuario = optUsuario.get();

        // 2. Validar Estado del Usuario
        if (Boolean.FALSE.equals(usuario.getEstado())) {
            return registrarYResponder(idUsuario, tipoEvento, false, "DENEGADO", 
                    "Usuario inactivo", "El usuario se encuentra inactivo en la plataforma", null);
        }

        // 3. Validar Suspensiones por la Guardia de Seguridad
        List<SuspensionPermiso> suspensiones = suspensionRepo.findByIdUsuarioAndActivoTrue(idUsuario);
        for (SuspensionPermiso s : suspensiones) {
            boolean posteriorAInicio = s.getFechaInicio() == null || !now.isBefore(s.getFechaInicio());
            boolean anteriorAFin = s.getFechaFin() == null || !now.isAfter(s.getFechaFin());
            if (posteriorAInicio && anteriorAFin) {
                return registrarYResponder(idUsuario, tipoEvento, false, "DENEGADO", 
                        "Permiso suspendido por Guardia (" + s.getMotivo() + ")", 
                        "Suspensión activa por motivo: " + s.getMotivo(), null);
            }
        }

        // Si el evento es SALIDA, se permite la salida de la sala registrar e informar
        if ("SALIDA".equalsIgnoreCase(tipoEvento)) {
            return registrarYResponder(idUsuario, tipoEvento, true, "PERMITIDO", 
                    "Salida registrada correctamente", null, null);
        }

        // 4. Si el usuario es Administrador o Guardia de Seguridad, acceso permitido por rol de supervisión
        String rol = usuario.getRol() != null ? usuario.getRol().toUpperCase() : "OPERARIO";
        if ("ADMINISTRADOR".equals(rol) || "GUARDIA_SEGURIDAD".equals(rol)) {
            return registrarYResponder(idUsuario, tipoEvento, true, "PERMITIDO",
                    "Acceso autorizado por supervisión institucional (" + rol + ")", null, null);
        }

        // 5. Consultar Cronograma Operativo del Día (Secretaría)
        LocalDate hoy = LocalDate.now();
        Optional<CronogramaOperativo> optCronograma = cronogramaRepo.findByFecha(hoy);

        if (optCronograma.isPresent()) {
            CronogramaOperativo cronograma = optCronograma.get();
            Optional<CategoriaMedicamento> optCat = categoriaRepo.findById(cronograma.getIdCategoria());

            if (optCat.isPresent()) {
                CategoriaMedicamento categoria = optCat.get();
                
                // Consultar Nivel de Acceso del Operario
                Optional<PerfilOperario> optPerfil = perfilRepo.findByIdUsuario(idUsuario);
                int nivelOperario = optPerfil.map(PerfilOperario::getNivelAcceso).orElse(1);

                // Evaluar Matriz de Permisos ABAC (Nivel vs Tipo de Medicamento)
                boolean permitido = evaluarMatrizPermisos(nivelOperario, categoria.getCodigo());

                if (!permitido) {
                    // Plan de Contingencia: Asignar Tarea Alternativa Automática
                    String tareaAlternativa = obtenerTareaAlternativaString();
                    String motivoRechazo = "Restricción por cronograma diario: La sala procesa " + 
                            categoria.getNombre() + " (" + categoria.getCodigo() + ") y el usuario tiene Nivel " + nivelOperario;

                    return registrarYResponder(idUsuario, tipoEvento, false, "DENEGADO", 
                            "Acceso denegado por cronograma", motivoRechazo, tareaAlternativa);
                }
            }
        }

        // 6. Permiso Concedido
        return registrarYResponder(idUsuario, tipoEvento, true, "PERMITIDO", 
                "Acceso permitido a room_911", null, null);
    }

    /**
     * Evalúa si un nivel de operario tiene permiso para manipular una categoría de medicamento:
     * - Operario Nivel 1: Tipo 1 y Tipo 2.
     * - Operario Nivel 2: Tipo 2 y Tipo 5.
     * - Operario Nivel 3: Acceso Global (Todos los tipos, incluyendo Medicamentos Especiales Tipo 4).
     */
    public boolean evaluarMatrizPermisos(int nivelOperario, String codigoCategoria) {
        if (codigoCategoria == null) return true;
        String cod = codigoCategoria.trim().toUpperCase();

        if (nivelOperario >= 3) {
            return true; // Nivel 3 tiene Acceso Global a todas las categorías
        }

        if (nivelOperario == 1) {
            return cod.equals("TIPO_1") || cod.equals("TIPO_2");
        }

        if (nivelOperario == 2) {
            return cod.equals("TIPO_2") || cod.equals("TIPO_5");
        }

        return false;
    }

    /**
     * Obtiene una tarea alternativa dinámica del plan de contingencia
     */
    private String obtenerTareaAlternativaString() {
        List<TareaAlternativa> tareas = tareaRepo.findByActivoTrue();
        if (!tareas.isEmpty()) {
            int randomIndex = (int) (Math.random() * tareas.size());
            return tareas.get(randomIndex).getDescripcion();
        }
        return "Acceso denegado: Asignado a investigación en Lab-B";
    }

    /**
     * Guarda el registro en la tabla de auditoría e instancia la respuesta DTO
     */
    private AccesoResponseDto registrarYResponder(String idUsuario, String tipoEvento, boolean permitido, 
                                                  String resultado, String mensaje, String motivoRechazo, 
                                                  String tareaAlternativa) {
        RegistroAuditoria audit = new RegistroAuditoria(idUsuario, tipoEvento, resultado, motivoRechazo, tareaAlternativa);
        auditoriaRepo.save(audit);

        return new AccesoResponseDto(permitido, resultado, mensaje, motivoRechazo, tareaAlternativa, idUsuario);
    }
}
