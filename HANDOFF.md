# Handoff de Room_911

## Hecho en esta entrega

- Consolidada la ejecución desde la raíz con scripts `pnpm`.
- Cambiado el login a identificación por ID interno, sin campo ni validación de contraseña.
- Implementadas entidades y SQL para `usuario`, `perfil`, `medicamento`, `cronograma`, `acceso` y `tarea_alternativa`.
- Implementado el motor ABAC: perfil + estado de permiso + cronograma + tipo de medicamento.
- Implementadas respuestas de denegación con tarea alternativa y persistencia de trazabilidad.
- Implementados endpoints de identificación, estado de usuario, cronograma, catálogo, evaluación y auditoría.
- Rediseñado el frontend con login, resumen, panel de guardia, agenda de secretaría y simulador de torniquete.
- Agregados mockup SVG, historias de usuario, UML, modelo relacional, API, reglas, guía y plan de estudio.
- Reestructurados los diagramas con la nomenclatura de la documentación original: módulos, actores `ADMINISTRADOR`, `GUARDIA_SEGURIDAD`, `OPERARIO`, `SECRETARÍA`, casos `CUxxx` y relaciones `include/extend`.
- Reescritas las historias de usuario con la plantilla original completa y agregada matriz de trazabilidad entre HU, CU, API y tablas.
- Agregado Docker Compose para inicializar PostgreSQL con semillas demo.
- Agregadas tres pruebas unitarias del motor ABAC para acceso permitido, tipo no autorizado con tarea alternativa y usuario suspendido.
- Agregada actualización `PUT /api/cronogramas/{id}` para completar el mantenimiento de franjas.
- Agregados filtros de auditoría, exportación CSV y pantalla de historial en el frontend.
- Agregada pantalla frontend de usuarios para registro, consulta y asignación de perfil; agregado `GET /api/perfiles`.
- Agregados filtros visuales de auditoría por ID de usuario y medicamento.
- Los filtros ahora se ejecutan mediante consulta parametrizada en PostgreSQL para resultado, acción, usuario, medicamento y rango de fechas.
- Agregado índice para `acceso.resultado`; la exportación CSV reutiliza los mismos filtros del backend.

## Pendiente de producto

- Integrar un proveedor real de identidad interna o lector físico; la identificación actual es deliberadamente una simulación del reto.
- Añadir autorización por sesión/token para producción. Actualmente los endpoints son públicos porque el reto pide login sin contraseña.
- Añadir exportación PDF y filtros visuales por rango de fechas en auditoría.
- Incorporar pruebas de integración con PostgreSQL y más pruebas de componentes del frontend.
- Definir una política formal de retención de logs y endurecer CORS antes de publicar en producción.

## Pendiente operativo

- Ejecutar `pnpm instalar`, `pnpm db:up` y `pnpm verificar` en el entorno final.
- El remoto fue verificado y existe en `main`; como su estructura ya contenía otra línea de desarrollo, la entrega se publicó sin sobrescribirla en la rama `codex/entrega-room-911`. Los cambios nuevos de filtros deben publicarse en esa rama y luego integrarse mediante Pull Request.
- Revisar los datos de demo y reemplazar correos de prueba.

## Siguiente sesión recomendada

1. Levantar la base y ejecutar la aplicación siguiendo `GUIA_INSTALACION.md`.
2. Probar los cinco casos de `AGENDA.md`.
3. Corregir los pendientes de producto según el alcance académico.
4. Hacer merge de `codex/entrega-room-911` mediante el Pull Request generado.
