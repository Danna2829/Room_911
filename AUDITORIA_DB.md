# Informe de Auditoría de Base de Datos - Proyecto room_911

**Fecha de Auditoría**: 28 de Agosto de 2026  
**Proyecto**: room_911 Backend (`/home/fabrica/Documentos/Reto_911/back`)  
**Base de Datos Objetivo**: PostgreSQL (`jdbc:postgresql://localhost:5432/Sala_911`)  

---

## 📌 1. Resumen Ejecutivo

Se realizó una auditoría técnica profunda sobre el modelo de persistencia y la configuración de base de datos del módulo Backend. Actualmente, la solución solo cuenta con **una única entidad JPA** (`Usuario`), la cual presenta serios hallazgos de diseño, nombres de columnas incompatibles con estándares PostgreSQL, y una ausencia completa de la estructura de tablas necesaria para soportar el control de acceso dinámico ABAC, cronogramas y auditoría requeridos por la especificación oficial del reto (`Reto_room_911_Extendido-1.pdf`).

---

## 🔍 2. Hallazgos y Vulnerabilidades Detectadas

### 🔴 Hallazgo 1: Columna con Carácter Especial (`contraseña`)
- **Archivo**: `back/src/main/java/com/example/demo/model/Usuario.java` (Línea 23)
- **Código**: `@Column(name = "contraseña")`
- **Impacto**: El uso del carácter en español `ñ` en nombres de columnas de base de datos causa fallas de sintaxis en PostgreSQL dependiendo del *charset/encoding* configurado en el servidor DB (ej. LATIN1 / SQL_ASCII vs UTF8) y complica consultas SQL nativas.
- **Recomendación**: Renombrar la columna física en la BD a `contrasena` manteniendo `@JsonProperty("contraseña")` en la capa DTO/JSON si se requiere compatibilidad con el frontend.

### 🔴 Hallazgo 2: Incompatibilidad en la Generación del ID de Usuario
- **Archivo**: `back/src/main/java/com/example/demo/service/UsuarioService.java` (Línea 21)
- **Código actual**: Genera IDs con prefijo `U` y 8 dígitos numéricos (ej. `U12345678`).
- **Especificación del PDF**: Los operarios utilizan ID Interno de expediente con formato empresarial (ej. `EMP-8821`, `EMP-099`).
- **Recomendación**: Actualizar la estrategia de generación de ID para alinearse al patrón `EMP-XXXX`.

### 🔴 Hallazgo 3: Ausencia del Modelo Relacional ABAC y Cronograma
- **Impacto**: El sistema actual **no puede evaluar permisos ABAC** ni ejecutar la lógica de negocio del reto porque carece de las siguientes entidades relacionales indispensables:
  1. `perfiles_operario` (Relación entre Usuario y Nivel 1, 2 o 3).
  2. `categorias_medicamento` (Tipos de medicamentos: Tipo 1, Tipo 2, Tipo 3, Tipo 4 Restringido, etc.).
  3. `cronograma_operativo` (Asignación diaria de categorías a la sala room_911).
  4. `suspensiones_permiso` (Gestión de guardias para suspender permisos por fecha/motivo).
  5. `tareas_alternativas` (Redirección automática de tareas en caso de denegación).
  6. `registros_auditoria` (Trazabilidad de entradas, salidas, rechazos y timestamps).

### 🟡 Hallazgo 4: Falta de Control de Versionamiento de Esquema DDL (Flyway/Liquibase)
- **Archivo**: `application.properties`
- **Configuración**: `spring.jpa.hibernate.ddl-auto=none`
- **Impacto**: No existen archivos SQL DDL ni scripts de migración en el repositorio. Si la base de datos se despliega en un nuevo entorno, la aplicación fallará al no encontrar las tablas.

---

## 🛠️ 3. Plan de Edición y Refactorización del Esquema SQL (Propuesta DDL)

Se debe ejecutar el siguiente script DDL oficial para corregir los hallazgos y crear la estructura relacional completa:

```sql
-- 1. Tabla de Usuarios (Corregida)
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario VARCHAR(20) PRIMARY KEY, -- ej. EMP-8821
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    rol VARCHAR(30) NOT NULL, -- ADMINISTRADOR, GUARDIA_SEGURIDAD, OPERARIO, SECRETARIA
    contrasena VARCHAR(255) NOT NULL, -- Nombre estandarizado sin 'ñ'
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 2. Perfiles de Operario (Niveles ABAC)
CREATE TABLE IF NOT EXISTS perfiles_operario (
    id SERIAL PRIMARY KEY,
    id_usuario VARCHAR(20) REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    nivel_acceso INT NOT NULL CHECK (nivel_acceso IN (1, 2, 3)),
    descripcion VARCHAR(255)
);

-- 3. Categorías de Medicamento
CREATE TABLE IF NOT EXISTS categorias_medicamento (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL, -- ej. TIPO_1, TIPO_2, TIPO_4
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    es_restringido BOOLEAN DEFAULT FALSE
);

-- 4. Cronograma Operativo Diario
CREATE TABLE IF NOT EXISTS cronograma_operativo (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    id_categoria INT REFERENCES categorias_medicamento(id),
    observaciones TEXT,
    CONSTRAINT unique_fecha_categoria UNIQUE (fecha, id_categoria)
);

-- 5. Suspensiones de Permiso (Panel Guardia)
CREATE TABLE IF NOT EXISTS suspensiones_permiso (
    id SERIAL PRIMARY KEY,
    id_usuario VARCHAR(20) REFERENCES usuarios(id_usuario),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP,
    motivo VARCHAR(255) NOT NULL, -- SANCIÓN, INCAPACIDAD, CAMBIO_TURNO
    activo BOOLEAN DEFAULT TRUE
);

-- 6. Tareas Alternativas (Plan de Contingencia)
CREATE TABLE IF NOT EXISTS tareas_alternativas (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255) NOT NULL
);

-- 7. Registros de Auditoría (Simulador Garita / Torniquete)
CREATE TABLE IF NOT EXISTS registros_auditoria (
    id SERIAL PRIMARY KEY,
    id_usuario VARCHAR(20) REFERENCES usuarios(id_usuario),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_evento VARCHAR(10) CHECK (tipo_evento IN ('ENTRADA', 'SALIDA')),
    resultado VARCHAR(15) CHECK (resultado IN ('PERMITIDO', 'DENEGADO')),
    motivo_rechazo TEXT,
    tarea_alternativa_asignada TEXT
);

-- Índices de Rendimiento
CREATE INDEX IF NOT EXISTS idx_usuarios_correo ON usuarios(correo);
CREATE INDEX IF NOT EXISTS idx_cronograma_fecha ON cronograma_operativo(fecha);
CREATE INDEX IF NOT EXISTS idx_auditoria_timestamp ON registros_auditoria(timestamp);
```

---

## 📌 4. Próximos Pasos en el Backend

1. Crear las entidades JPA correspondientes (`PerfilOperario`, `CategoriaMedicamento`, `CronogramaOperativo`, `SuspensionPermiso`, `TareaAlternativa`, `RegistroAuditoria`).
2. Implementar repositorios de Spring Data JPA para cada entidad.
3. Crear el componente `ABACEngineService` para la evaluación dinámica de permisos.

---

# Auditoría #2 — 3 de Septiembre de 2026 (BD viva + evaluación de esquema propuesto)

**Alcance**: inspección de la BD real `Sala_911` en PostgreSQL (podman `postgres-dev`, puerto 5432), verificación del dominio contra `Documentación/Reto_room_911_Extendido.pdf`, y evaluación de la propuesta de reemplazo de esquema (catálogo `roles`, tablas `empleados`, `usuarios` normalizadas, `medicamentos`, `registro_accesos`, `eventos_especiales`).

## 1. Estado de la BD viva (hallazgos)

| # | Hallazgo | Severidad | Acción tomada |
|---|----------|-----------|---------------|
| 1 | Cuentas semilla `EMP-0000/0001/0002` quedaron con `estado = false` (desactivadas a las 14:13–14:14 vía borrado lógico del panel de Usuarios, presumiblemente al probar la UI). Con eso el login admin devolvía 403. | 🔴 Crítica | Reactivadas con `UPDATE usuarios SET estado = TRUE`. |
| 2 | `cronograma_operativo` permitía programar la misma categoría dos veces el mismo día (la auditoría #1 proponía `UNIQUE (fecha, id_categoria)` pero nunca se aplicó). Evaluación ABAC ambigua con duplicados. | 🔴 Alta | Creado índice único **parcial** `uq_cronograma_fecha_categoria_activo ON (fecha, id_categoria) WHERE activo = TRUE` (parcial para no bloquear reprogramación tras soft-delete). |
| 3 | `registros_auditoria.tipo_evento` / `resultado` sin dominio de valores (cualquier string era válido). | 🟡 Media | Añadidos `CHECK` `chk_tipo_evento` (ENTRADA/SALIDA) y `chk_resultado` (PERMITIDO/DENEGADO). |
| 4 | `perfiles_operario.id_usuario` nullable (un perfil huérfano rompería el motor ABAC). | 🟡 Media | `ALTER COLUMN ... SET NOT NULL`. |
| 5 | Faltaban índices de consulta para guardia (`findByIdUsuarioAndActivoTrue`) y trazabilidad por usuario. | 🟢 Baja | Creados `idx_suspensiones_usuario_activo` e `idx_auditoria_usuario`. |
| 6 | `schema.sql` no reflejaba estos endurecimientos → deriva de esquema entre entornos (no hay Flyway/Liquibase). | 🟡 Media | Cambios replicados en `schema.sql` de forma idempotente; `mvnw test` 10/10 tras el cambio. |

**Pendientes estructurales** (de auditoría anterior, siguen vigentes): contraseñas en texto plano sin BCrypt; `SecurityConfig` con `permitAll`; sin versionado de esquema (Flyway); `spring.sql.init.continue-on-error=true` enmascara fallos de migración en arranque.

## 2. Evaluación del esquema propuesto (¿migrar?)

**Recomendación: NO migrar de forma integral al esquema propuesto.** Comparado con los requisitos del PDF del reto, el esquema actual cubre más y el propuesto obligaría a reescribir ~8 entidades JPA, 7 controladores, el motor ABAC y 6 vistas del frontend, para terminar con **menos cobertura funcional**:

| Requisito del reto (PDF) | Esquema actual | Esquema propuesto |
|---|---|---|
| Cronograma operativo diario por categoría (Sección 3, núcleo del ABAC) | ✅ `cronograma_operativo` | ❌ **No existe** — el requisito central sería imposible sin añadirla |
| Suspensión de permisos por **rango de fechas** (sanción, incapacidad, cambio de turno) | ✅ `suspensiones_permiso` (fecha_inicio/fin) | ⚠️ Solo `empleados.estado 'ACTIVO'/'SUSPENDIDO'` — no soporta rangos |
| Redirección de tareas alternativas (contingencia) | ✅ `tareas_alternativas` + campo en auditoría | ❌ Solo texto libre `detalles` |
| Registro de trazabilidad con resultado y motivo | ✅ `registros_auditoria` (con FK) | ⚠️ `registro_accesos` (sin FK a empleado) |
| Autenticación web de panels (admin/guardia/secretaría) | ✅ correo + contraseña + recuperación por token | ✅ username + contraseña (pero pierde la recuperación ya implementada) |
| Roles del sistema | ⚠️ string libre en `usuarios.rol` | ⚠️ Catálogo `roles` mejor normalizado, pero **omite SECRETARIA y SUPERADMINISTRADOR** (ya implementados) |
| Inventario de medicamentos (módulo extendido del frontend) | ✅ `inventario_medicamentos` con lotes y movimientos | ❌ No existe (solo catálogo `medicamentos`) |
| Categorías con bandera de restringido / soft-delete | ✅ `categorias_medicamento` | ⚠️ `medicamentos.tipo INT` sin banderas ni ciclo de vida |

**Ideas del esquema propuesto que SÍ valen la pena adoptar incrementalmente** (como evolución, no como reemplazo):
1. **Catálogo `roles`** en lugar del string libre `usuarios.rol` (normalización RBAC; hay que incluir `SUPERADMINISTRADOR` y `SECRETARIA`).
2. **Separar `empleados` (persona/expediente EMP-XXXX) de `usuarios` (cuenta de acceso)**: coincide con el dominio del reto, donde el operario entra por ID interno y los paneles web usan credenciales.
3. `eventos_especiales` como bitácora de eventos de guardia distintos al acceso (hojas de incidentes).
4. Nombres ASCII (`password`) — ya se corrigió en el esquema actual (`contrasena`).

## 3. Cambios aplicados (BD viva + `schema.sql`)

Los seis fixes de la sección 1 fueron aplicados a la BD `Sala_911` y replicados en `back/src/main/resources/schema.sql`. Verificación: `mvnw test` → `Tests run: 10, Failures: 0, Errors: 0` (BUILD SUCCESS).
