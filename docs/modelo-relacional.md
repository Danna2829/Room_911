# Modelo relacional

El script ejecutable es [`database/schema.sql`](../database/schema.sql). Las tablas cumplen los nombres solicitados.

| Tabla | Clave primaria | Claves foráneas | Propósito |
|---|---|---|---|
| `perfil` | `id` | — | Nivel y tipos de medicamento autorizados. |
| `usuario` | `id_usuario` | `perfil_id → perfil.id` | Identidad, rol, estado y suspensión temporal. |
| `medicamento` | `id` | — | Catálogo de sustancias y tipo de riesgo. |
| `cronograma` | `id` | `medicamento_id → medicamento.id` | Día, franja y actividad operativa. |
| `tarea_alternativa` | `id` | — | Actividades de contingencia activas. |
| `acceso` | `id` | `usuario_id → usuario.id_usuario`; `medicamento_id → medicamento.id` | Auditoría de entrada/salida y decisión. |

Reglas de integridad: nivel entre 1 y 3; hora final posterior a la inicial; suspensión final posterior o igual a la inicial; `resultado` solo puede ser `PERMITIDO` o `DENEGADO`; `accion` acepta `ENTRADA`, `SALIDA` o `IDENTIFICACION`. La tabla `acceso` tiene índices por fecha, usuario y resultado para auditoría.
