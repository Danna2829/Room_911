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
