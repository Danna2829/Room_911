# Plan de estudio e implementación — 15 días

Este plan permite que otra persona retome el proyecto con otra IA o continúe el desarrollo sin perder contexto.

| Día | Estudio y objetivo | Evidencia |
|---|---|---|
| 1 | Leer el reto, actores, riesgos y alcance. | Historias HU-01 a HU-08. |
| 2 | Modelar relaciones y restricciones en PostgreSQL. | `database/schema.sql`. |
| 3 | Revisar Spring Boot, JPA y controladores REST. | Paquetes en `back/Sala_911`. |
| 4 | Implementar perfiles y matriz de tipos. | Entidad `Perfil` + HU-03. |
| 5 | Implementar suspensión y vigencia temporal. | Endpoint de estado. |
| 6 | Construir algoritmo de cronograma por día/hora. | `AccesoServicio`. |
| 7 | Agregar tareas alternativas y trazabilidad. | Tablas `tarea_alternativa` y `acceso`. |
| 8 | Estudiar React, estado y consumo REST. | `Aplicacion911`. |
| 9 | Construir identificación y resumen. | Login + resumen. |
| 10 | Construir pantalla de guardia. | Activar/suspender. |
| 11 | Construir pantalla de secretaría. | Crear franjas. |
| 12 | Construir simulador de garita. | Evaluación visible. |
| 13 | Probar permitidos, denegados y rangos. | Casos manuales de API. |
| 14 | Revisar accesibilidad, errores y responsive. | Build y revisión visual. |
| 15 | Documentar, versionar y publicar. | README, handoff y GitHub. |

## Casos de estudio sugeridos

1. `EMP-301` con `M001` en lunes dentro de la franja: permitido.
2. `EMP-301` con `M003` el martes: denegado por nivel y tarea alternativa.
3. Suspender `EMP-303`, evaluar acceso: denegado por estado.
4. Crear una franja desde secretaría y volver a evaluar en el horario correspondiente.
5. Consultar `/api/accesos` y comprobar que cada intento tiene fecha, acción y resultado.
