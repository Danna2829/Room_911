# Room_911

Sistema de control de acceso dinámico para un área restringida de medicamentos. La decisión de acceso combina identificación por ID interno, perfil de riesgo, estado del permiso y cronograma operativo.

## Estado de la entrega

La aplicación incluye un backend Spring Boot + PostgreSQL, un frontend React, scripts reproducibles de base de datos, cuatro pantallas principales, motor ABAC, tareas alternativas, trazabilidad y documentación del reto. La publicación en GitHub queda preparada con el remoto objetivo, pero requiere que la persona propietaria autentique GitHub desde su equipo.

## Estructura

```text
room_911/
├── back/Sala_911/             Backend Spring Boot (Java 17)
├── front/                     Frontend React (comandos pnpm)
├── database/                  schema.sql y seeds.sql para PostgreSQL
├── docs/                      Historias, mockups, UML, modelo y API
├── AGENDA.md                  Plan de estudio/implementación de 15 días
├── HANDOFF.md                 Cambios hechos, pendientes y siguiente paso
├── GUIA_INSTALACION.md        Puesta en marcha local
└── docker-compose.yml         PostgreSQL local reproducible
```

## Inicio rápido

Requisitos: Node.js 20+, pnpm 9+, Java 17+, Maven 3.9+ y Docker Desktop.

```bash
pnpm instalar
pnpm db:up
pnpm backend:dev
# en otra terminal
pnpm frontend:dev
```

Frontend: http://localhost:3000 · Backend: http://localhost:8080/api/salud

IDs demo: `EMP-301` (operario Nivel 1), `EMP-302` (operario Nivel 2), `EMP-303` (operario Nivel 3), `GUA-001` (guardia), `SEC-001` (secretaría). No se usa contraseña.

Para validar antes de entregar:

```bash
pnpm verificar
```

## Documentación

- [Guía de instalación](GUIA_INSTALACION.md)
- [Historias de usuario](docs/historias-usuario.md)
- [Mockups](docs/mockups.md)
- [Diagramas UML](docs/uml.md)
- [Modelo relacional](docs/modelo-relacional.md)
- [API y reglas de negocio](docs/api.md)
- [Mockup visual](docs/mockups/mockups-room-911.svg)
- [Agenda de estudio](AGENDA.md)
- [Handoff](HANDOFF.md)

## Publicación en GitHub

El repositorio objetivo es `https://github.com/Danna2829/Room_911.git`. Desde la raíz, una vez autenticada GitHub:

```bash
git init
git add .
git commit -m "feat: implementar Room 911"
git branch -M main
git remote add origin https://github.com/Danna2829/Room_911.git
git push -u origin main
```
