-- Datos Iniciales (Seed Data) para room_911

-- 1. Categorías de Medicamento
INSERT INTO categorias_medicamento (codigo, nombre, descripcion, es_restringido)
VALUES 
('TIPO_1', 'Medicamento Tipo 1', 'Sustancias estándar de bajo riesgo', FALSE),
('TIPO_2', 'Medicamento Tipo 2', 'Sustancias de manipulación intermedia', FALSE),
('TIPO_3', 'Medicamento Tipo 3', 'Sustancias avanzadas', FALSE),
('TIPO_4', 'Medicamento Tipo 4 (Restringido)', 'Sustancias de alto control y máximo riesgo', TRUE),
('TIPO_5', 'Medicamento Tipo 5', 'Sustancias especializadas de tipo 5', FALSE)
ON CONFLICT (codigo) DO NOTHING;

-- 2. Tareas Alternativas (Plan de Contingencia)
INSERT INTO tareas_alternativas (codigo, descripcion)
VALUES 
('TASK_LAB_B', 'Acceso denegado: Asignado a investigación en Lab-B'),
('TASK_CLIENTES', 'Acceso denegado: Asignado a atención a clientes'),
('TASK_INVENTARIO', 'Acceso denegado: Asignado a inventario general en bodega C')
ON CONFLICT (codigo) DO NOTHING;

-- 3. Usuarios de Pruebas
INSERT INTO usuarios (id_usuario, nombre, apellido, correo, rol, contrasena, estado)
VALUES
('EMP-0001', 'Admin', 'Sistema', 'admin@farmaceutica.com', 'ADMINISTRADOR', 'admin123', TRUE),
('EMP-0002', 'Carlos', 'Guardia', 'guardia@farmaceutica.com', 'GUARDIA_SEGURIDAD', 'guardia123', TRUE),
('EMP-8821', 'Juan', 'Operario Uno', 'operario1@farmaceutica.com', 'OPERARIO', 'operario123', TRUE),
('EMP-8822', 'Maria', 'Operario Dos', 'operario2@farmaceutica.com', 'OPERARIO', 'operario123', TRUE),
('EMP-8823', 'Pedro', 'Operario Tres', 'operario3@farmaceutica.com', 'OPERARIO', 'operario123', TRUE)
ON CONFLICT (id_usuario) DO NOTHING;

-- 3b. Superadministrador (jerarquía superior de la plataforma)
INSERT INTO usuarios (id_usuario, nombre, apellido, correo, rol, contrasena, estado)
VALUES ('EMP-0000', 'Super', 'Administrador', 'superadmin@farmaceutica.com', 'SUPERADMINISTRADOR', 'super123', TRUE)
ON CONFLICT (id_usuario) DO NOTHING;

-- 4. Perfiles de Operario
INSERT INTO perfiles_operario (id_usuario, nivel_acceso, descripcion)
VALUES
('EMP-8821', 1, 'Operario Nivel 1 - Acceso a Tipo 1 y 2'),
('EMP-8822', 2, 'Operario Nivel 2 - Acceso a Tipo 2 y 5'),
('EMP-8823', 3, 'Operario Nivel 3 - Acceso Global (Incluye Tipo 4 Restringido)')
ON CONFLICT DO NOTHING;
