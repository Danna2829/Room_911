# Diagramas UML y trazabilidad del diseño

Los diagramas conservan la estructura de la documentación original: límites `Sistema Room_911`, módulos `Gestionar_usuarios`, `Gestionar_permisos_accesos`, `Gestionar_cronograma` y `Gestionar_reportes`, actores por rol y casos identificados con `CUxxx` dentro de cada módulo.

## Entregables visuales

- [Diagrama general de casos de uso](diagramas/diagrama-casos-uso.svg)
- [Diagrama de proceso de acceso](diagramas/diagrama-proceso-acceso.svg)
- [Diagrama de clases](diagramas/diagrama-clases.svg)
- [Modelo relacional visual](diagramas/modelo-relacional.svg)
- [Fuentes editables Mermaid](diagramas/README.md)

## Decisiones de coherencia

- `ADMINISTRADOR` administra usuarios, perfiles y auditoría.
- `GUARDIA_SEGURIDAD` consulta personas, activa o suspende permisos y monitorea intentos.
- `OPERARIO` se identifica, consulta su información, solicita acceso y recibe la decisión.
- `SECRETARÍA` crea, edita y consulta el cronograma operativo.
- El `MOTOR ABAC` valida perfil, tipo de medicamento, fecha, hora y estado del permiso.
- `CU002 Restablecer contraseña` se conserva como referencia de la plantilla original, pero queda marcado fuera de alcance porque el reto exige autenticación únicamente por ID interno y sin contraseñas.

## Diagrama de proceso: acceso según cronograma

```mermaid
flowchart TD
 A[ID interno + acción + medicamento] --> B{¿Usuario registrado?}
 B -- No --> Z[Denegar: ID no registrado]
 B -- Sí --> C{¿Permiso activo en la fecha?}
 C -- No --> D[Denegar: permiso suspendido]
 C -- Sí --> E{¿Franja activa hoy y ahora?}
 E -- No --> F[Denegar: fuera de cronograma]
 E -- Sí --> G{¿Perfil permite el tipo?}
 G -- No --> H[Denegar + tarea alternativa]
 G -- Sí --> I[Permitir entrada o salida]
 Z --> J[Registrar trazabilidad]
 D --> J
 F --> J
 H --> J
 I --> J
 J --> K[Responder al torniquete]
```

## Diagrama de clases

```mermaid
classDiagram
 class Usuario { +String idUsuario +String nombre +String apellido +String rol +boolean activo +estaSuspendido(fecha) }
 class Perfil { +Long id +String nombre +int nivel +String tiposPermitidos +permiteTipo(tipo) }
 class Medicamento { +String id +String nombre +int tipo }
 class Cronograma { +Long id +String diaSemana +LocalTime horaInicio +LocalTime horaFin +String actividad }
 class RegistroAcceso { +Long id +LocalDateTime fechaHora +String accion +String resultado +String motivo +String tareaAlternativa }
 class TareaAlternativa { +Long id +String nombre +String descripcion +boolean activa }
 Usuario "*" --> "1" Perfil : tiene
 Cronograma "*" --> "1" Medicamento : programa
 RegistroAcceso "*" --> "0..1" Usuario : identifica
 RegistroAcceso "*" --> "0..1" Medicamento : evalua
 RegistroAcceso "*" --> "0..1" TareaAlternativa : deriva
```
