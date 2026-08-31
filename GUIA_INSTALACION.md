# Guía de instalación y ejecución

## 1. Requisitos

- Node.js 20 o superior y `pnpm` 9.
- Java 17 y Maven 3.9 o superior.
- Docker Desktop con Docker Compose.
- Puerto 3000, 5432 y 8080 disponibles.

Aunque el backend utiliza Maven internamente, los comandos de operación del proyecto están expuestos únicamente mediante scripts `pnpm` desde la raíz.

## 2. Instalar dependencias

```bash
pnpm instalar
```

Si tu instalación de pnpm intenta resolver dependencias desde una carpeta raíz sin `node_modules`, también puedes ejecutar el flujo desde el workspace del frontend:

```bash
pnpm --dir front install
```

## 3. Crear PostgreSQL

```bash
pnpm db:up
```

Equivalente directo dentro del workspace: `pnpm --dir front run db:up`.

El contenedor crea la base `room_911`, usuario `room911`, contraseña de desarrollo `room911_dev`, tablas y datos de ejemplo. Para reiniciar la base desde cero, detén el contenedor y elimina el volumen `room_911_pgdata` desde Docker Desktop.

## 4. Ejecutar

Terminal 1:

```bash
pnpm backend:dev
```

Equivalente: `pnpm --dir front run backend:dev`.

Terminal 2:

```bash
pnpm frontend:dev
```

Abre http://localhost:3000 e ingresa uno de los IDs demo. Si se necesita otra base, define `DB_URL`, `DB_USER` y `DB_PASSWORD` antes de ejecutar `pnpm backend:dev`.

## 5. Verificar

```bash
pnpm frontend:test
pnpm verificar
```

El backend expone `GET /api/salud`. El frontend consume `http://localhost:8080/api`; puede cambiarse con `REACT_APP_API_URL`.

## 6. Solución rápida de problemas

- `Connection refused 5432`: ejecuta `pnpm db:up` y espera a que el healthcheck termine.
- `relation ... does not exist`: la base se creó con un volumen anterior; reiníciala desde Docker Desktop para volver a ejecutar `schema.sql` y `seeds.sql`.
- `ID no registrado`: utiliza `EMP-301`, `EMP-302`, `EMP-303` o `GUA-001`.
- El acceso depende de la fecha y hora actuales. Para una demostración determinista, utiliza la franja de lunes incluida en las semillas o modifica el cronograma desde la pantalla de secretaría.
