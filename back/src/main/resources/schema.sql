-- Script DDL de Estructura de Base de Datos para room_911

ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_rol_check;

-- Catalogo de roles (RBAC). usuarios.id_rol referencia este catalogo y
-- la columna usuarios.rol se mantiene sincronizada a nivel de aplicacion.
CREATE TABLE IF NOT EXISTS roles (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO roles (nombre) VALUES
    ('SUPERADMINISTRADOR'), ('ADMINISTRADOR'), ('GUARDIA_SEGURIDAD'), ('SECRETARIA'), ('OPERARIO')
ON CONFLICT (nombre) DO NOTHING;

CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) UNIQUE NOT NULL,
    rol VARCHAR(50) NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    estado BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS token_reset VARCHAR(255);
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS token_expiracion TIMESTAMP;

-- Vinculo con el catalogo de roles (se añade si el despliegue es previo al catalogo).
ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS id_rol INT REFERENCES roles(id_rol);
UPDATE usuarios u SET id_rol = r.id_rol FROM roles r WHERE u.rol = r.nombre AND u.id_rol IS NULL;

-- Bitacora de eventos especiales registrados por la Guardia.
CREATE TABLE IF NOT EXISTS eventos_especiales (
    id_evento SERIAL PRIMARY KEY,
    id_usuario VARCHAR(20) REFERENCES usuarios(id_usuario),
    tipo_evento VARCHAR(100) NOT NULL,
    descripcion TEXT,
    fecha_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Registro de empleados (persona / expediente interno EMP-XXXX), separado de la
-- cuenta de acceso (usuarios). Se sincroniza a nivel de aplicacion en UsuarioService.
CREATE TABLE IF NOT EXISTS empleados (
    id_empleado VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    nivel INT CHECK (nivel IN (1, 2, 3)),
    estado VARCHAR(20) DEFAULT 'ACTIVO',
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE usuarios ADD COLUMN IF NOT EXISTS id_empleado VARCHAR(20) REFERENCES empleados(id_empleado);

-- Migracion idempotente: crea el expediente de cada cuenta y lo vincula.
INSERT INTO empleados (id_empleado, nombre, apellido, nivel, estado)
SELECT u.id_usuario, u.nombre, u.apellido, p.nivel_acceso,
       CASE WHEN u.estado THEN 'ACTIVO' ELSE 'SUSPENDIDO' END
FROM usuarios u
LEFT JOIN perfiles_operario p ON p.id_usuario = u.id_usuario
WHERE NOT EXISTS (SELECT 1 FROM empleados e WHERE e.id_empleado = u.id_usuario);

UPDATE usuarios SET id_empleado = id_usuario
WHERE id_empleado IS NULL
  AND EXISTS (SELECT 1 FROM empleados e WHERE e.id_empleado = usuarios.id_usuario);

CREATE TABLE IF NOT EXISTS perfiles_operario (
    id SERIAL PRIMARY KEY,
    id_usuario VARCHAR(20) UNIQUE REFERENCES usuarios(id_usuario) ON DELETE CASCADE,
    nivel_acceso INT NOT NULL CHECK (nivel_acceso IN (1, 2, 3)),
    descripcion VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS categorias_medicamento (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(20) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    es_restringido BOOLEAN DEFAULT FALSE,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS cronograma_operativo (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    id_categoria INT REFERENCES categorias_medicamento(id),
    observaciones TEXT,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS suspensiones_permiso (
    id SERIAL PRIMARY KEY,
    id_usuario VARCHAR(20) REFERENCES usuarios(id_usuario),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP,
    motivo VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS tareas_alternativas (
    id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) UNIQUE NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    activo BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS registros_auditoria (
    id SERIAL PRIMARY KEY,
    id_usuario VARCHAR(20) REFERENCES usuarios(id_usuario),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_evento VARCHAR(50) NOT NULL,
    resultado VARCHAR(15) NOT NULL,
    motivo_rechazo TEXT,
    tarea_alternativa_asignada TEXT
);

CREATE TABLE IF NOT EXISTS inventario_medicamentos (
    id SERIAL PRIMARY KEY,
    id_categoria INT REFERENCES categorias_medicamento(id),
    cantidad INT NOT NULL,
    tipo_movimiento VARCHAR(20) NOT NULL,
    lote VARCHAR(50) NOT NULL,
    observaciones TEXT,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

CREATE INDEX IF NOT EXISTS idx_usuarios_correo ON usuarios(correo);
CREATE INDEX IF NOT EXISTS idx_cronograma_fecha ON cronograma_operativo(fecha);
CREATE INDEX IF NOT EXISTS idx_auditoria_timestamp ON registros_auditoria(timestamp);

-- Endurecimiento del esquema (auditoria 2026-09-03): idempotentes para arranques repetidos.

-- Un unico cronograma activo por (fecha, categoria); el indice parcial permite
-- conservar filas inhabilitadas (activo = FALSE) sin bloquear nuevas programaciones.
CREATE UNIQUE INDEX IF NOT EXISTS uq_cronograma_fecha_categoria_activo
    ON cronograma_operativo (fecha, id_categoria) WHERE activo = TRUE;

-- Un perfil de operario siempre pertenece a un usuario.
ALTER TABLE perfiles_operario ALTER COLUMN id_usuario SET NOT NULL;

-- Dominio de valores en el registro de auditoria.
ALTER TABLE registros_auditoria ADD CONSTRAINT chk_tipo_evento CHECK (tipo_evento IN ('ENTRADA','SALIDA'));
ALTER TABLE registros_auditoria ADD CONSTRAINT chk_resultado CHECK (resultado IN ('PERMITIDO','DENEGADO'));

-- Indices de consulta para guardia y trazabilidad.
CREATE INDEX IF NOT EXISTS idx_suspensiones_usuario_activo ON suspensiones_permiso (id_usuario, activo);
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON registros_auditoria (id_usuario);
