# Diagramas UML

Las vistas visuales están disponibles en la carpeta [`docs/diagramas`](diagramas/README.md):

- [Diagrama de casos de uso](diagramas/diagrama-casos-uso.svg)
- [Diagrama de proceso de acceso](diagramas/diagrama-proceso-acceso.svg)
- [Diagrama de clases](diagramas/diagrama-clases.svg)

Los archivos `.mmd` de esa carpeta son las fuentes editables para Mermaid.

Los diagramas están expresados en Mermaid para poder versionarlos y renderizarlos en GitHub, Mermaid Live o cualquier visor compatible.

## Casos de uso

```mermaid
flowchart LR
  empleado([Empleado])
  guardia([Guardia])
  secretaria([Secretaría])
  admin([Administrador])
  login((Identificarse por ID))
  sim((Simular entrada/salida))
  revisar((Consultar estado y resultado))
  suspender((Activar/suspender permisos))
  agenda((Publicar cronograma))
  auditoria((Consultar trazabilidad))
  empleado --> login --> sim
  empleado --> revisar
  guardia --> suspender
  guardia --> revisar
  secretaria --> agenda
  admin --> auditoria
  sim -. incluye .-> evaluar((Evaluar matriz ABAC))
  evaluar -. incluye .-> alterna((Asignar tarea alternativa si deniega))
```

## Diagrama de proceso: acceso según cronograma

```mermaid
flowchart TD
 A[ID interno + acción] --> B{¿Usuario registrado?}
 B -- No --> Z[Denegar y mostrar ID no registrado]
 B -- Sí --> C{¿Permiso activo en la fecha?}
 C -- No --> D[Denegar: permiso suspendido]
 C -- Sí --> E{¿Existe franja activa hoy y ahora?}
 E -- No --> F[Denegar: fuera de cronograma]
 E -- Sí --> G{¿Tipo permitido por perfil?}
 G -- No --> H[Denegar + tarea alternativa]
 G -- Sí --> I[Permitir entrada/salida]
 D --> J[Registrar trazabilidad]
 F --> J
 H --> J
 I --> J
 Z --> J
```

## Diagrama de clases

```mermaid
classDiagram
 class Usuario { +String idUsuario +String nombre +String apellido +boolean activo +estaSuspendido() }
 class Perfil { +Long id +String nombre +int nivel +String tiposPermitidos +permiteTipo() }
 class Medicamento { +String id +String nombre +int tipo }
 class Cronograma { +Long id +String diaSemana +LocalTime horaInicio +LocalTime horaFin +String actividad }
 class Acceso { +Long id +LocalDateTime fechaHora +String accion +String resultado +String motivo }
 class TareaAlternativa { +Long id +String nombre +String descripcion +boolean activa }
 Usuario "*" --> "1" Perfil : tiene
 Cronograma "*" --> "1" Medicamento : programa
 Acceso "*" --> "0..1" Usuario : identifica
 Acceso "*" --> "0..1" Medicamento : evalua
 Acceso "0..*" --> "0..1" TareaAlternativa : deriva en
```
