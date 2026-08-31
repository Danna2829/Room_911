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
- Agregado Docker Compose para inicializar PostgreSQL con semillas demo.
- Agregadas tres pruebas unitarias del motor ABAC para acceso permitido, tipo no autorizado con tarea alternativa y usuario suspendido.
- Agregada actualización `PUT /api/cronogramas/{id}` para completar el mantenimiento de franjas.

## Pendiente de producto

- Integrar un proveedor real de identidad interna o lector físico; la identificación actual es deliberadamente una simulación del reto.
- Añadir autorización por sesión/token para producción. Actualmente los endpoints son públicos porque el reto pide login sin contraseña.
- Añadir exportación CSV/PDF y filtros avanzados de auditoría.
- Incorporar pruebas de integración con PostgreSQL y más pruebas de componentes del frontend.
- Definir una política formal de retención de logs y endurecer CORS antes de publicar en producción.

## Pendiente operativo

- Ejecutar `pnpm instalar`, `pnpm db:up` y `pnpm verificar` en el entorno final.
- El remoto fue verificado y existe en `main`; como su estructura ya contenía otra línea de desarrollo, la entrega se publicó sin sobrescribirla en la rama `codex/entrega-room-911`. Los cambios actuales deben volver a publicarse en esa rama y luego integrarse mediante Pull Request.
- Revisar los datos de demo y reemplazar correos de prueba.

## Siguiente sesión recomendada

1. Levantar la base y ejecutar la aplicación siguiendo `GUIA_INSTALACION.md`.
2. Probar los cinco casos de `AGENDA.md`.
3. Corregir los pendientes de producto según el alcance académico.
4. Hacer commit y publicar el repositorio.
