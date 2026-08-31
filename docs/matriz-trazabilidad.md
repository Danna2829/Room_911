# Matriz de trazabilidad

Esta matriz conecta la plantilla de historias, los casos de uso de la documentación, los endpoints y las tablas que sostienen cada flujo.

| Historia | Caso de uso / módulo | API principal | Tablas | Estado |
|---|---|---|---|---|
| HU-001 | CU001 / Gestionar_usuarios | `POST /api/usuarios/identificar` | `usuario`, `perfil` | Implementada |
| HU-002 | CU002 / Gestionar_usuarios | — | — | Fuera de alcance |
| HU-003 | CU003 / Gestionar_usuarios | `POST /api/usuarios` | `usuario`, `perfil` | API |
| HU-004 | CU004 / Gestionar_usuarios | `GET /api/usuarios/{id}` | `usuario`, `perfil` | API |
| HU-005 | CU001, CU005 / Gestionar_permisos_accesos | `POST /api/usuarios` | `usuario`, `perfil`, `medicamento` | Modelo/API |
| HU-006 | CU008, CU010 / Gestionar_usuarios | `PATCH /api/usuarios/{id}/estado` | `usuario` | Implementada |
| HU-007 | CU001 / Gestionar_permisos_accesos | `POST /api/accesos/evaluar` | `acceso` | Implementada |
| HU-008 | CU002, CU006 / Gestionar_permisos_accesos | `POST /api/accesos/evaluar` | `usuario`, `perfil`, `cronograma`, `medicamento`, `acceso` | Implementada |
| HU-009 | CU003 / Gestionar_permisos_accesos | respuesta de evaluación | `acceso` | Implementada |
| HU-010 | CU005 / Gestionar_cronograma | respuesta de evaluación | `tarea_alternativa`, `acceso` | Implementada |
| HU-011 | CU001, CU002, CU003 / Gestionar_cronograma | `GET/POST/PUT/DELETE /api/cronogramas` | `cronograma`, `medicamento` | API/UI parcial |
| HU-012 | CU001, CU002, CU003 / Gestionar_reportes | `GET /api/accesos/filtrar`, `GET /api/accesos/exportar.csv` | `acceso` | Implementada |

## Reglas compartidas

1. El acceso se identifica por ID interno, sin contraseña.
2. La decisión requiere usuario registrado y permiso activo.
3. Debe existir una franja vigente para el día y la hora evaluados.
4. El tipo del medicamento debe estar permitido por el perfil.
5. Toda decisión, permitida o denegada, queda en `acceso`.
6. La denegación selecciona una tarea activa cuando existe.
