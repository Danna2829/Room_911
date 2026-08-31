INSERT INTO perfil (nombre,nivel,tipos_permitidos) VALUES
 ('Operario Nivel 1',1,'1,2'), ('Operario Nivel 2',2,'2,5'), ('Operario Nivel 3',3,'1,2,3,4,5')
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO medicamento (id,nombre,descripcion,tipo,unidad_medida) VALUES
 ('M001','Paracetamol','Analgésico de uso común',1,'Tabletas 500 mg'),
 ('M002','Amoxicilina','Antibiótico de amplio espectro',2,'Cápsulas 500 mg'),
 ('M003','Morfina','Medicamento de alto control',4,'Ampollas 10 mg'),
 ('M004','Ibuprofeno','Antiinflamatorio no esteroideo',5,'Tabletas 400 mg')
ON CONFLICT (id) DO NOTHING;

INSERT INTO usuario (id_usuario,nombre,apellido,correo,rol,perfil_id) VALUES
 ('EMP-301','Carlos','López','carlos@sala911.test','OPERARIO',(SELECT id FROM perfil WHERE nivel=1)),
 ('EMP-302','María','García','maria@sala911.test','OPERARIO',(SELECT id FROM perfil WHERE nivel=2)),
 ('EMP-303','Juan','Torres','juan@sala911.test','OPERARIO',(SELECT id FROM perfil WHERE nivel=3)),
 ('GUA-001','Valentina','Ríos','guardia@sala911.test','GUARDIA',(SELECT id FROM perfil WHERE nivel=3)),
 ('SEC-001','Andrés','Mora','secretaria@sala911.test','SECRETARIA',(SELECT id FROM perfil WHERE nivel=3))
ON CONFLICT (id_usuario) DO NOTHING;

INSERT INTO cronograma (dia_semana,hora_inicio,hora_fin,medicamento_id,actividad) VALUES
 ('Lunes','00:00','23:59','M001','Recepción y alistamiento'),
 ('Martes','08:00','12:00','M003','Manipulación de medicamento de alto control'),
 ('Miércoles','14:00','18:00','M004','Despacho controlado'),
 ('Jueves','06:00','14:00','M002','Preparación de lote')
ON CONFLICT DO NOTHING;

INSERT INTO tarea_alternativa (nombre,descripcion) VALUES
 ('Investigación en Lab-B','Apoyar la revisión documental del lote en Lab-B.'),
 ('Atención a clientes','Apoyar solicitudes y trazabilidad desde atención a clientes.')
ON CONFLICT DO NOTHING;
