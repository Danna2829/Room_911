# Guía de cambios actual — Room_911

Este archivo reemplaza la guía histórica de la plantilla, que describía un login con contraseñas y una base H2. La implementación vigente usa identificación por ID interno y PostgreSQL.

## Qué revisar primero

1. `database/schema.sql` y `database/seeds.sql`: fuente de verdad del modelo y datos demo.
2. `back/Sala_911/src/main/java/org/example/sala911/servicio/AccesoServicio.java`: motor ABAC.
3. `front/src/componentes/Aplicacion911.js`: pantallas operativas.
4. `GUIA_INSTALACION.md`, `AGENDA.md` y `HANDOFF.md`: ejecución y continuidad.

## Flujo actual

`ID interno → perfil → estado de permiso → cronograma día/hora → tipo permitido → permitir/denegar → tarea alternativa → registro en acceso`.

## Comandos del proyecto

Usa los scripts documentados en la raíz con `pnpm`. Si la instalación local de pnpm exige un workspace instalado, usa el equivalente `pnpm --dir front ...` indicado en la guía.
