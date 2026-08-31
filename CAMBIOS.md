# Registro de cambios — Room_911

## Línea base recibida

El proyecto tenía una pantalla administrativa genérica, autenticación por ID más contraseña, H2 en memoria, entidades sin perfil relacional y documentación parcial. El Git existente estaba únicamente dentro de `front`; la raíz no tenía un historial utilizable.

## Cambios aplicados

- Se creó la estructura ejecutable de raíz con scripts `pnpm`, Docker Compose, guía y README.
- Se migró la configuración principal de H2 a PostgreSQL mediante variables `DB_URL`, `DB_USER` y `DB_PASSWORD`.
- Se modelaron las tablas requeridas: `perfil`, `usuario`, `medicamento`, `cronograma`, `tarea_alternativa` y `acceso`.
- Se reemplazó la contraseña por identificación mediante ID interno.
- Se construyó el motor ABAC con nivel, tipo de medicamento, horario y suspensión.
- Se agregó registro de permitidos, denegados, motivo y tarea alternativa.
- Se agregaron endpoints REST de identidad, agenda, catálogo, evaluación y auditoría.
- Se reemplazó la experiencia React por login, resumen, guardia, secretaría y simulador.
- Se agregaron historias de usuario, mockup SVG, diagramas UML Mermaid, modelo relacional, API, reglas, agenda y handoff.

## Verificación realizada

- `pnpm --dir front build`: correcto.
- `mvn -f back/Sala_911/pom.xml package -DskipTests`: correcto.
- Contexto Spring + JPA con H2 en modo no web: correcto; las seis entidades se inicializan.
- Docker/PostgreSQL: no fue posible ejecutarlo en este entorno porque el sandbox no tiene acceso al socket Docker; debe comprobarse en el equipo de entrega.

## Pendientes conocidos

Consulta [HANDOFF.md](HANDOFF.md) para pendientes de producto, seguridad de producción, pruebas de integración, exportación y publicación en GitHub.
