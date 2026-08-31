# Historias de usuario Room_911

Este documento conserva la plantilla entregada: código, nombre, complejidad, historia relacionada, módulo, descripción, requerimiento, criterios de aceptación, tareas y control de versiones. Las historias usan la forma estándar **Como [rol], quiero [funcionalidad], para [beneficio]** y se relacionan con los `CUxxx` del [diagrama de casos de uso](uml.md).

> Decisión del reto: la identificación usa únicamente ID interno. La historia original de contraseña se conserva como requerimiento fuera de alcance y no se implementa.

## HU-001 — Identificarse por ID interno

**Complejidad:** Media · **HU relacionada:** N/A · **Módulo:** Gestionar_usuarios · **CU:** CU001 · **Estado:** Implementada

**Descripción:** Como **operario, guardia de seguridad, secretaría o administrador**, quiero **identificarme con mi ID interno**, para **acceder a las funciones que corresponden a mi rol sin administrar contraseñas**.

**Requerimiento:** El sistema recibe el ID, comprueba que exista y que el usuario esté activo, y devuelve sus datos básicos y perfil. Si no existe o está inactivo, informa el motivo y no permite continuar.

**Criterios de aceptación:**

- Dado un ID registrado y activo, cuando se envía, entonces se muestra el usuario y su perfil.
- Dado un ID inexistente, cuando se envía, entonces aparece “Usuario no registrado”.
- Dado un usuario inactivo, cuando se envía, entonces se deniega la identificación.
- El formulario no solicita contraseña.

**Tareas:** Diseñar formulario; validar campo obligatorio; consultar usuario; mostrar errores; guardar el contexto de sesión de la interfaz; probar ID válido, inexistente e inactivo.

## HU-002 — Restablecer contraseña

**Complejidad:** Media · **HU relacionada:** HU-001 · **Módulo:** Gestionar_usuarios · **CU:** CU002 · **Estado:** Fuera de alcance

**Descripción:** Como **usuario**, quiero **restablecer mi contraseña**, para **recuperar mi acceso cuando la olvide**.

**Requerimiento:** Esta historia proviene de la plantilla original, pero no se implementa porque el reto define autenticación sin contraseña, mediante ID interno. La implementación futura requeriría correo, código de verificación, expiración y políticas de contraseña.

**Criterios de aceptación:** Queda como pendiente de una decisión de alcance; no debe aparecer como formulario activo en la versión actual.

**Tareas:** Definir proveedor de identidad; diseñar recuperación; implementar código temporal; proteger contraseñas; crear pruebas de expiración y repetición.

## HU-003 — Registrar usuario

**Complejidad:** Alta · **HU relacionada:** HU-001, HU-005 · **Módulo:** Gestionar_usuarios · **CU:** CU003 · **Estado:** Implementada

**Descripción:** Como **administrador**, quiero **registrar un usuario con rol y perfil**, para **asignarle permisos controlados dentro de Room_911**.

**Requerimiento:** El registro debe exigir ID interno, nombre, apellido, rol y perfil existente. El correo, si se informa, debe tener formato válido. El usuario inicia activo y queda persistido en `usuario`.

**Criterios de aceptación:**

- Campos obligatorios incompletos producen un mensaje de validación.
- Un perfil inexistente no puede asociarse.
- El usuario creado queda relacionado con `perfil` mediante `perfil_id`.
- El registro se puede consultar posteriormente por su ID.

**Tareas:** Crear formulario administrativo; validar datos; implementar alta REST; asociar perfil; registrar auditoría administrativa; probar duplicidad de ID.

## HU-004 — Consultar usuario

**Complejidad:** Media · **HU relacionada:** HU-001 · **Módulo:** Gestionar_usuarios · **CU:** CU004 · **Estado:** Implementada

**Descripción:** Como **administrador o guardia de seguridad**, quiero **consultar usuarios y su estado**, para **verificar quién puede solicitar acceso**.

**Requerimiento:** El administrador consulta todos los usuarios. El guardia consulta los datos operativos mínimos: ID, rol, perfil y estado. El operario consulta únicamente su propia identificación.

**Criterios de aceptación:** Un ID inexistente devuelve “Usuario no encontrado”; el estado activo o suspendido es visible; los datos sensibles no se muestran al guardia.

**Tareas:** Crear listado; agregar búsqueda por ID, rol y estado; limitar columnas por rol; mostrar estados; probar usuario inexistente.

## HU-005 — Asignar rol y perfil de riesgo

**Complejidad:** Alta · **HU relacionada:** HU-003 · **Módulo:** Gestionar_permisos_accesos · **CU:** CU001 y CU005 · **Estado:** Implementada

**Descripción:** Como **administrador**, quiero **asignar un rol y un perfil de nivel de riesgo**, para **determinar qué tipos de medicamento puede manipular una persona**.

**Requerimiento:** Los perfiles Nivel 1, Nivel 2 y Nivel 3 definen tipos permitidos. El perfil se almacena como FK en `usuario`; la evaluación consulta sus tipos autorizados.

**Criterios de aceptación:** Nivel 1 permite tipos 1 y 2; Nivel 2 permite tipos 2 y 5; Nivel 3 permite todos; un tipo no autorizado genera denegación.

**Tareas:** Mantener catálogo de perfiles; validar nivel 1..3; asignar perfil; mostrar perfil en panel; probar cada matriz.

## HU-006 — Activar, desactivar y suspender usuario

**Complejidad:** Media · **HU relacionada:** HU-004 · **Módulo:** Gestionar_usuarios · **CU:** CU008 y CU010 · **Estado:** Implementada

**Descripción:** Como **guardia de seguridad**, quiero **activar o suspender el permiso de un usuario de inmediato o por fechas**, para **responder a novedades operativas sin cambiar su perfil permanente**.

**Requerimiento:** El guardia puede cambiar `activo` y, opcionalmente, definir `suspendido_desde` y `suspendido_hasta`. La base exige un rango válido.

**Criterios de aceptación:** Un usuario suspendido dentro del rango es denegado; fuera del rango puede evaluarse; fechas inválidas son rechazadas; el estado persiste.

**Tareas:** Crear control de estado; validar fechas; implementar PATCH de estado; reflejar el resultado en el motor; probar activación, suspensión y vencimiento.

## HU-007 — Solicitar acceso en torniquete

**Complejidad:** Media · **HU relacionada:** HU-001, HU-008 · **Módulo:** Gestionar_permisos_accesos · **CU:** CU001 · **Estado:** Implementada

**Descripción:** Como **operario**, quiero **solicitar entrada o salida indicando el medicamento**, para **saber si puedo atravesar la garita en el contexto actual**.

**Requerimiento:** El simulador envía ID interno, acción, medicamento opcional y fecha/hora opcional. El motor devuelve permitido o denegado y el motivo.

**Criterios de aceptación:** Las acciones válidas son ENTRADA y SALIDA; una solicitud se registra aunque sea denegada; si no se indica medicamento se toma el de la franja activa.

**Tareas:** Crear pantalla de torniquete; selector de acción; selector de medicamento; conectar POST `/api/accesos/evaluar`; mostrar respuesta; probar entrada y salida.

## HU-008 — Validar acceso por matriz ABAC

**Complejidad:** Alta · **HU relacionada:** HU-005, HU-006, HU-007 · **Módulo:** Gestionar_permisos_accesos · **CU:** CU002, CU006 · **Estado:** Implementada

**Descripción:** Como **motor de control de acceso**, quiero **evaluar usuario, perfil, estado, fecha, hora y medicamento**, para **tomar una decisión dinámica y trazable**.

**Requerimiento:** La decisión sigue este orden: usuario registrado, permiso activo, franja vigente y tipo permitido por perfil. Una sola condición inválida deniega.

**Criterios de aceptación:** Nivel 3 puede acceder a Tipo 4 cuando está programado; Nivel 1 y 2 reciben denegación; fuera de cronograma se deniega; cada decisión se persiste en `acceso`.

**Tareas:** Implementar servicio ABAC; normalizar días en español; validar horas; consultar cronograma; persistir resultado; cubrir casos permitidos y denegados.

## HU-009 — Recibir validación de acceso

**Complejidad:** Baja · **HU relacionada:** HU-007, HU-008 · **Módulo:** Gestionar_permisos_accesos · **CU:** CU003 · **Estado:** Implementada

**Descripción:** Como **operario**, quiero **recibir una respuesta clara del motor**, para **saber si continúo hacia la sala o sigo la instrucción alternativa**.

**Requerimiento:** La respuesta muestra `permitido`, `resultado`, `motivo`, medicamento evaluado, tarea alternativa si aplica y fecha/hora.

**Criterios de aceptación:** La interfaz diferencia PERMITIDO y DENEGADO; el motivo no queda vacío cuando se deniega; la trazabilidad contiene timestamp.

**Tareas:** Diseñar estados visuales; mapear DTO; mostrar motivo; mostrar timestamp; probar respuesta de cada rama.

## HU-010 — Recibir opción alternativa

**Complejidad:** Media · **HU relacionada:** HU-008, HU-009 · **Módulo:** Gestionar_cronograma · **CU:** CU005 · **Estado:** Implementada

**Descripción:** Como **operario con acceso denegado**, quiero **recibir una tarea alternativa**, para **continuar mi jornada sin ingresar al área restringida**.

**Requerimiento:** Ante una denegación por cronograma, perfil o suspensión, el motor selecciona una tarea activa de `tarea_alternativa` y la devuelve con el registro.

**Criterios de aceptación:** Existe una tarea cuando hay tareas activas; aparece en la pantalla de torniquete; se guarda el texto y la FK de la tarea en `acceso`.

**Tareas:** Crear catálogo; seleccionar tarea activa; devolverla en DTO; mostrarla en frontend; probar denegación por Tipo 4 y fuera de horario.

## HU-011 — Crear, editar y consultar cronograma

**Complejidad:** Alta · **HU relacionada:** HU-008, HU-010 · **Módulo:** Gestionar_cronograma · **CU:** CU001, CU002, CU003 · **Estado:** API y vista principal implementadas

**Descripción:** Como **secretaría**, quiero **publicar medicamento, día, franja y actividad**, para **definir el contexto operativo que usa la matriz ABAC**.

**Requerimiento:** Cada franja relaciona un medicamento y valida que hora final sea posterior a hora inicial. El motor solo considera franjas del día y la hora actuales.

**Criterios de aceptación:** Se puede crear, editar, consultar y eliminar una franja; una franja inválida se rechaza; el cronograma muestra tipo y horario.

**Tareas:** Crear formulario; listar agenda; implementar POST, PUT, GET y DELETE; validar horas; probar cambio de medicamento y franja.

## HU-012 — Monitorear y exportar auditoría

**Complejidad:** Media · **HU relacionada:** HU-006, HU-008 · **Módulo:** Gestionar_reportes · **CU:** CU001, CU002, CU003 · **Estado:** Implementada

**Descripción:** Como **guardia o administrador**, quiero **consultar, filtrar y exportar los intentos de acceso**, para **investigar incidentes y demostrar trazabilidad**.

**Requerimiento:** El historial ordena por fecha descendente y permite filtrar por resultado, acción, ID, medicamento y rango de fechas. CSV conserva los mismos filtros.

**Criterios de aceptación:** Cada fila muestra fecha, usuario, acción, resultado, medicamento y motivo; los filtros se ejecutan en PostgreSQL; la descarga genera un archivo CSV.

**Tareas:** Crear tabla; implementar consulta parametrizada; agregar índices; crear endpoint CSV; conectar panel; probar filtros y denegaciones.

## Control de versiones

| Versión | Fecha | Autor | Revisión | Descripción | Aprobador |
|---|---|---|---|---|---|
| 1.0 | 2026-08-31 | Danna Arévalo / Room_911 | Inicial | Alineación de historias con plantilla, diagramas y motor ABAC | Pendiente |
