-- Script DDL de Estructura de Base de Datos para room_911

ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_rol_check;

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
