# 🚀 Guía de Retoma Rápida — room_911

> Guía para levantar y continuar el proyecto desde otra PC (casa) en el menor tiempo posible.
> Fecha de última actualización: 3 de septiembre de 2026.

---

## 1. ¿En qué estado está el proyecto?

**Funcionando y verificado:**
- ✅ Stack completo: React (frontend) + Spring Boot 4.1.1 (backend) + PostgreSQL
- ✅ Seguridad: contraseñas cifradas con **BCrypt** (migración automática al arrancar) y **JWT** con roles validados en el servidor
- ✅ Paneles por rol: Admin/Superadmin → Panel General, Secretaría → Cronograma, Guardia → Monitor, Operario → Torniquete (con contingencia a los 3 intentos fallidos)
- ✅ CRUDs completos de usuarios, medicamentos, cronograma, inventario y suspensiones (con soft delete y reactivar)
- ✅ Reportes exportables: CSV, **Excel** y **PDF**
- ✅ Carga masiva de usuarios por CSV
- ✅ Recuperación de contraseña con código de verificación visible 15 segundos
- ✅ Dashboard del Panel General con datos reales de la API
- ✅ Base de datos con catálogo `roles`, tabla `empleados` separada de `usuarios`, eventos de guardia

**Los detalles completos de todo lo hecho están en `HANDOFFS.md` (entradas #013 a #020) y la auditoría de BD en `AUDITORIA_DB.md`.**

---

## 2. Requisitos según cómo quieras arrancar

| Opción | Necesitas |
|--------|-----------|
| **A. Docker (más rápido)** | Docker Desktop (o podman + compose) |
| **B. Manual (entorno de desarrollo)** | JDK 17, Node ≥ 20, pnpm, PostgreSQL |

> ⚠️ Opción A: el `docker-compose.yml` existe desde antes y **no se ha re-verificado** con las últimas versiones del código. Si falla, usa la opción B.

---

## 3. Opción A — Docker (1 comando)

```bash
git clone git@github.com:Danna2829/Room_911.git
cd Room_911
docker compose up -d --build
```
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- PostgreSQL: localhost:5433 (contenedor) / localhost:5432 para el backend (red interna)

---

## 4. Opción B — Manual paso a paso (lo probado en esta máquina)

### 4.1 PostgreSQL
```bash
# Con podman (Linux) o docker (Windows/Mac):
podman run -d --name postgres-dev \
  -e POSTGRES_PASSWORD=fabrica2024* \
  -e POSTGRES_USER=postgres \
  -p 5432:5432 docker.io/library/postgres:17

# Crear la base de datos (el backend la llena solo con schema.sql + data.sql):
podman exec postgres-dev psql -U postgres -c 'CREATE DATABASE "Sala_911";'
```

### 4.2 Backend (puerto 8080)
```bash
cd Room_911/back
./mvnw spring-boot:run        # en Windows: mvnw.cmd spring-boot:run
```
- Requiere **JDK 17+**. Verifica con `java -version` y `javac -version` (si `javac` no existe, solo tienes el JRE: instala `default-jdk`/`java-17-openjdk-devel`, o descarga Temurin 17 desde adoptium.net).
- La primera vez tarda unos minutos descargando dependencias de Maven.
- Al arrancar **migra solo** las contraseñas a BCrypt y crea las tablas (schema.sql es idempotente).

### 4.3 Frontend (puerto 3000)
```bash
cd Room_911/Front/room911-frontend
npm install -g pnpm    # si no tienes pnpm
pnpm install
pnpm start
```

---

## 5. Cuentas de prueba (ya sembradas en la BD)

| Rol | Correo | Contraseña | Aterriza en |
|-----|--------|------------|-------------|
| SUPERADMINISTRADOR | superadmin@farmaceutica.com | super123 | /dashboard |
| ADMINISTRADOR | admin@farmaceutica.com | admin123 | /dashboard |
| GUARDIA_SEGURIDAD | guardia@farmaceutica.com | guardia123 | /monitor |
| SECRETARIA | *(no hay semilla; se crea desde Usuarios)* | — | /cronograma |
| OPERARIO | operario1@farmaceutica.com | operario123 | /garita |

> Para probar el torniquete de operario necesitas un **cronograma del día**: entra como Secretaría/Admin → Cronograma → "Programar día" con la fecha de hoy y una categoría.
> Operarios de prueba: EMP-8821 (Nivel 1), EMP-8822 (Nivel 2), EMP-8823 (Nivel 3).

---

## 6. Problemas comunes

| Síntoma | Causa | Solución |
|---------|-------|----------|
| `release version 17 not supported` al compilar | Solo tienes JRE sin `javac` | Instala el JDK (ver 4.2) o apunta `JAVA_HOME` a un JDK 17 |
| Backend arranca pero el login da error de red/CORS | Backend no está arriba en 8080 | Verifica `curl http://localhost:8080/api/categorias` (401 sin token es correcto) |
| 401 en todas las peticiones desde el frontend | Sesión expirada | Cierra sesión y vuelve a entrar (el token dura 8 h) |
| Puerto 5432 ocupado | Otro Postgres local | Usa otro puerto en el contenedor y arranca el backend con `DB_URL=jdbc:postgresql://localhost:PUERTO/Sala_911` |
| Cambié contraseñas y ya no sé cuáles | — | Borra la fila del usuario y reinicia el backend: `data.sql` la vuelve a crear activa con su contraseña de prueba |

---

## 7. Pendientes (por dónde seguir)

1. **CI en GitHub Actions** — se quitó temporalmente (fallaba `pnpm install` en el runner; el job del backend pasaba completo ✅). El historial lo tiene: `git log --diff-filter=D -- .github/` para recuperarlo y depurarlo.
2. **Renovación de token** — la sesión JWT dura 8 h; opcionalmente implementar refresh.
3. **@ControllerAdvice** ya existe (`GlobalExceptionHandler`); revisar si falta cobertura en algún flujo nuevo.
4. **Docker compose** re-verificar con el código actual (Opción A).
5. Datos demo que puedes borrar desde la app: usuario "Ana Torres" (EMP-3607).

---

## 8. Documentación del proyecto

- `HANDOFFS.md` — bitácora detallada de todo lo trabajado (#001 a #020)
- `AUDITORIA_DB.md` — auditorías de base de datos (2) y justificación del esquema actual
- `README.md` — descripción general y design system del frontend
- `HISTORIAS_DE_USUARIO.md`, `DOMINIO.md`, `PLAN_DE_ACCION.md` — dominio y planeación
- `Documentación/Reto_room_911_Extendido.pdf` — especificación oficial del reto
