CREATE TABLE IF NOT EXISTS perfil (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(40) NOT NULL UNIQUE,
  nivel INTEGER NOT NULL CHECK (nivel BETWEEN 1 AND 3),
  tipos_permitidos VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS usuario (
  id_usuario VARCHAR(30) PRIMARY KEY,
  nombre VARCHAR(80) NOT NULL,
  apellido VARCHAR(80) NOT NULL,
  correo VARCHAR(160),
  rol VARCHAR(20) NOT NULL DEFAULT 'OPERARIO',
  perfil_id BIGINT NOT NULL REFERENCES perfil(id),
  activo BOOLEAN NOT NULL DEFAULT TRUE,
  suspendido_desde DATE,
  suspendido_hasta DATE,
  CHECK (suspendido_hasta IS NULL OR suspendido_desde IS NULL OR suspendido_hasta >= suspendido_desde)
);

CREATE TABLE IF NOT EXISTS medicamento (
  id VARCHAR(20) PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  descripcion VARCHAR(255),
  tipo INTEGER NOT NULL CHECK (tipo > 0),
  unidad_medida VARCHAR(80)
);

CREATE TABLE IF NOT EXISTS cronograma (
  id BIGSERIAL PRIMARY KEY,
  dia_semana VARCHAR(15) NOT NULL,
  hora_inicio TIME NOT NULL,
  hora_fin TIME NOT NULL,
  medicamento_id VARCHAR(20) NOT NULL REFERENCES medicamento(id),
  actividad VARCHAR(255),
  CHECK (hora_fin > hora_inicio)
);

CREATE TABLE IF NOT EXISTS tarea_alternativa (
  id BIGSERIAL PRIMARY KEY,
  nombre VARCHAR(120) NOT NULL,
  descripcion VARCHAR(255) NOT NULL,
  activa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS acceso (
  id BIGSERIAL PRIMARY KEY,
  usuario_id VARCHAR(30) REFERENCES usuario(id_usuario),
  fecha_hora TIMESTAMP NOT NULL,
  accion VARCHAR(15) NOT NULL CHECK (accion IN ('ENTRADA','SALIDA','IDENTIFICACION')),
  resultado VARCHAR(15) NOT NULL CHECK (resultado IN ('PERMITIDO','DENEGADO')),
  motivo VARCHAR(255),
  tarea_alternativa VARCHAR(255),
  tarea_alternativa_id BIGINT REFERENCES tarea_alternativa(id),
  medicamento_id VARCHAR(20) REFERENCES medicamento(id),
  direccion_ip VARCHAR(45)
);

CREATE INDEX IF NOT EXISTS idx_acceso_fecha ON acceso(fecha_hora DESC);
CREATE INDEX IF NOT EXISTS idx_acceso_usuario ON acceso(usuario_id);
CREATE INDEX IF NOT EXISTS idx_cronograma_dia ON cronograma(dia_semana);
