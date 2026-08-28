# Proyecto room_911 - Sistema de Control de Acceso Dinámico (ABAC / RBAC)

Sistema integral de gestión y control de acceso dinámico por Matriz de Riesgo y Cronograma Operativo para la sala restringida **room_911** de un laboratorio farmacéutico.

---

## 📌 Requisitos Previos y Reglas (`AGENTS.md`)
- **Backend**: Java 17+, Maven 3.x, PostgreSQL 13+.
- **Frontend**: Node.js v18+, **`pnpm` obligado** (`npm` está estrictamente prohibido por norma del proyecto).

---

## ⚡ Instalación Rápida con Docker (recomendado)

Todo el stack (PostgreSQL + Backend + Frontend) levanta con **un solo comando**, sin instalar Java, Maven ni Node localmente.

```bash
# 1. (Opcional) Configura tus variables de entorno
cp .env.example .env      # edita el password si lo deseas

# 2. Levanta el stack completo
docker compose up -d

# 3. Verifica
docker compose ps
```

- **Frontend**: http://localhost:3000
- **Backend (API)**: http://localhost:8080
- **PostgreSQL**: localhost:5432 (BD `Sala_911`)

Para detener y eliminar contenedores: `docker compose down` (los datos de la BD persisten en el volumen `pgdata`).
Para borrar también la BD: `docker compose down -v`.

> **Seguridad**: Las credenciales se toman de `.env` (ver `.env.example`). El archivo `.env` real **no** se sube al repo. En producción cambia `DB_PASSWORD` por un secreto fuerte y no reutilices la contraseña de desarrollo.

### 🖥️ Si es una PC nueva (instalar Docker)

El proyecto ya trae `docker-compose.yml` con **PostgreSQL, Backend y Frontend**. Solo necesitas Docker; **no** hace falta instalar Java, Maven, Node ni PostgreSQL en la PC.

**1. Instalar Docker (Engine + plugin Compose):**
- **Windows / macOS**: descarga e instala **Docker Desktop** desde https://www.docker.com/products/docker-desktop/ (o sigue https://docs.docker.com/get-docker/). Al terminar, abre Docker Desktop.
- **Linux (Ubuntu/Debian)**:
  ```bash
  sudo apt-get update
  sudo apt-get install -y docker.io docker-compose-plugin
  sudo usermod -aG docker $USER   # luego cierra sesión y vuelve a entrar
  ```
- **Verificar** (debe mostrar versiones):
  ```bash
  docker --version
  docker compose version
  ```

**2. Clonar y levantar el stack:**
```bash
git clone https://github.com/Danna2829/Room_911.git
cd Room_911
cp .env.example .env          # opcional: define tu DB_PASSWORD
docker compose up -d --build  # primera vez: descarga imágenes y compila (puede tardar minutos)
```
- **Frontend**: http://localhost:3000  ·  **Backend**: http://localhost:8080  ·  **PostgreSQL**: localhost:5432 (BD `Sala_911`)
- Login de prueba: `admin@farmaceutica.com` / `admin123`

**3. Comandos útiles:**
- Ver estado: `docker compose ps`
- Ver logs del backend: `docker compose logs -f back`
- Detener (conserva la BD): `docker compose down`
- Reset total de la BD: `docker compose down -v`

> **Reinicios seguros**: `schema.sql` usa `CREATE TABLE IF NOT EXISTS` / `DROP CONSTRAINT IF EXISTS` y `data.sql` usa `INSERT ... ON CONFLICT DO NOTHING`, por lo que el backend puede reiniciarse con el volumen persistente (`docker compose down` seguido de `docker compose up -d`) sin errores.

---

## 🚀 Cómo Ejecutar el Proyecto (desarrollo local, sin Docker)

> Requiere: **Java 17+**, **Maven 3.x** (o usa `./mvnw` incluido), **Node 18+** con **`pnpm`** (`npm` prohibido por `AGENTS.md`).
> La configuración de BD se lee de variables de entorno (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`) con valores por defecto para desarrollo local.

### 1. Inicialización de Base de Datos PostgreSQL
Asegúrese de tener PostgreSQL en ejecución en `localhost:5432` con la base de datos `Sala_911`:
```sql
CREATE DATABASE "Sala_911";
```
*(Los scripts `schema.sql` y `data.sql` se ejecutarán automáticamente al iniciar el Backend)*.

### 2. Ejecución del Backend (Spring Boot)
Desde la raíz del repo:
```bash
cd back
./mvnw spring-boot:run      # o: mvn spring-boot:run
```
- **Puerto**: `http://localhost:8080`
- **Pruebas unitarias**: `mvn test`

### 3. Ejecución del Frontend (React con `pnpm`)
Desde la raíz del repo:
```bash
cd Front/room911-frontend
pnpm install
pnpm start
```
- **Puerto**: `http://localhost:3000`
- **Build de producción**: `pnpm build`

---

## 👥 Cuentas de Prueba Pre-configuradas

| ID Expediente | Nombre | Rol | Nivel Operario (ABAC) | Contraseña | Permisos ABAC |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **EMP-0001** | Admin Sistema | `ADMINISTRADOR` | N/A | `admin123` | Gestión global y administración |
| **EMP-0002** | Carlos Guardia | `GUARDIA_SEGURIDAD` | N/A | `guardia123` | Control de suspensiones y monitor |
| **EMP-8821** | Juan Operario 1 | `OPERARIO` | **Nivel 1** | `operario123` | Acceso a Medicamentos Tipo 1 y 2 |
| **EMP-8822** | Maria Operario 2 | `OPERARIO` | **Nivel 2** | `operario123` | Acceso a Medicamentos Tipo 2 y 5 |
| **EMP-8823** | Pedro Operario 3 | `OPERARIO` | **Nivel 3** | `operario123` | **Acceso Global** (Incluye Tipo 4 Restringido) |

---

## 🔌 Principales Endpoints REST (API Backend)

- **Torniquete Garita**: `POST /api/acceso/evaluar` (`{ "idUsuario": "EMP-8821", "tipoEvento": "ENTRADA" }`)
- **Monitor en Vivo**: `GET /api/acceso/monitor`
- **Secretaría (Cronograma)**: `POST /api/cronograma`, `GET /api/cronograma/hoy`
- **Guardia (Suspensiones)**: `POST /api/guardia/suspender`, `GET /api/guardia/suspensiones`
- **Administración Usuarios**: `POST /api/admin/crear-usuario`, `GET /api/admin/listar-usuarios`
- **Inventario**: `GET /api/inventario`, `POST /api/inventario/entrada`
- **Exportar Reportes**: `GET /api/reportes/exportar/csv`

---

## 📑 Documentación de Apoyo
- `DOMINIO.md`: Modelo relacional y diagramas del reto.
- `AUDITORIA_DB.md`: Informe de auditoría de BD y scripts DDL.
- `HISTORIAS_DE_USUARIO.md`: Fichas detalladas de las 28 HUs.
- `PLAN_DE_ACCION.md`: Cronograma por fases del sprint de 15 días.
- `HANDOFFS.md`: Bitácora de trazabilidad e historial continuo.

---

## 🎨 Frontend — Design System (Room_911 UI)

Stack: **React 19 + Vite/CRA + Bootstrap 5 (SCSS theming) + Bootstrap Icons + React Router**. `npm` está prohibido; usar siempre `pnpm`.

### Instalación
```bash
cd Front/room911-frontend
pnpm install
pnpm start          # dev  -> http://localhost:3000
pnpm build          # producción
```
> **Nota (pnpm + build scripts)**: En la primera instalación pnpm puede bloquear scripts de build por política de supply-chain. Si `pnpm install` falla, ejecuta una vez:
> ```bash
> pnpm approve-builds --all
> ```

### Tokens y tema
- Paleta de marca en `src/styles/_tokens.scss` (azul navy / azul / celeste / blanco / negro).
- Overrides de Bootstrap en `src/styles/_bootstrap-theme.scss`.
- Estilos de componentes y layout en `_components.scss` / `_layout.scss`; punto de entrada: `src/styles/index.scss`.

### Librería de componentes (`src/components/ui/`)
Estándar reutilizable para todas las vistas:
- `Button` (variantes `primary|soft|outline|dark|danger|...`, `loading`, `icon`)
- `TextField`, `SelectField`, `Slider` (con ícono e validación)
- `Modal` (sin JS de Bootstrap, accesible por teclado)
- `Alert` (éxito/info/warning/danger)
- `Toast` + `useToast()` (feedback global con `ToastProvider`)
- `StatusPill`, `Spinner`, `EmptyState`, `StatCard`
- `DataTable` (buscador global, filtros por columna y ordenamiento — patrón CRUD estándar)
- `PageHeader`, `Icon`

### Layout (`src/components/layout/`)
`AppLayout` = `Sidebar` (navegación por secciones) + `Topbar` (búsqueda global y acciones) + contenido con `max-width` estandarizado. Las vistas se enrutan con React Router (`/login`, `/dashboard`, `/usuarios`, `/inventario`, etc.).

### Estado actual
- ✅ Fundación del design system, `AppLayout`/shell con `Sidebar` + `Topbar` (logout) y `Login` (pantalla dividida, validación + toast).
- ✅ **Autenticación real**: `AuthContext` + `RequireAuth` conectados a `POST /api/auth/login` (campos `correo`/`contraseña`). Sesión en `localStorage`.
- ✅ Vistas funcionales conectadas a la API (todas sobre `DataTable`/`Modal`/`Toast`/`StatusPill`):
  - `Garita` → `POST /api/acceso/evaluar` (motor ABAC) + historial de sesión.
  - `Cronograma` → `GET/POST /api/cronograma` + `GET /api/categorias`.
  - `Monitor` → `GET /api/acceso/monitor` + suspensiones (`POST /api/guardia/suspender`, `GET /api/guardia/suspensiones`, revocar).
  - `Usuarios` → CRUD `GET/POST/PUT/DELETE /api/admin/*`.
  - `Inventario` → `GET /api/inventario`, `POST /api/inventario/entrada|salida`, `GET /api/categorias`.
  - `Reportes` → `GET /api/reportes/accesos` + exportar CSV (`GET /api/reportes/exportar/csv`).
- ✅ `Dashboard` demo usando `StatCard` + `DataTable` + `Modal`.
- ⚠️ `pnpm build` compila limpio; aparecen 3 avisos cosméticos de `postcss-svgo` por los data-URI SVG de Bootstrap Icons (no afectan el resultado ni los íconos, que usan fuente).

### Cómo probar end-to-end
```bash
# Terminal 1 — stack completo (requiere Docker)
docker compose up -d            # db + backend + frontend
# Abrir http://localhost:3000  (frontend)  — login con admin@farmaceutica.com / admin123

# O bien, solo el backend con Docker y el frontend local:
docker compose up -d db back
cd Front/room911-frontend && pnpm install && pnpm start
```


