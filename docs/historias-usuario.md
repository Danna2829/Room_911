# Historias de usuario

Todas siguen el formato solicitado: **Como [rol], quiero [funcionalidad], para [beneficio]**.

## HU-01 — Identificación sin contraseña

Como **empleado**, quiero **identificarme mediante mi ID interno**, para **solicitar acceso sin administrar contraseñas en la simulación**.

**Criterios de aceptación:** dado un ID registrado, cuando lo envío, entonces el sistema muestra mi perfil; dado un ID inexistente, cuando lo envío, entonces informa que no está registrado; ningún formulario de acceso solicita contraseña.

## HU-02 — Evaluación dinámica en garita

Como **empleado**, quiero **simular entrada o salida**, para **conocer si mi acceso es válido en el contexto operativo actual**.

**Criterios:** la evaluación considera ID, acción, medicamento programado, fecha/hora y estado; una respuesta muestra permitido o denegado; cada intento válido queda en `acceso`.

## HU-03 — Permisos por nivel de riesgo

Como **responsable de seguridad**, quiero **que cada perfil tenga tipos de medicamento autorizados**, para **reducir la exposición a sustancias no relacionadas con su función**.

**Criterios:** Nivel 1 permite tipos 1 y 2; Nivel 2 permite tipos 2 y 5; Nivel 3 permite todos; un tipo no autorizado deniega y explica el motivo.

## HU-04 — Suspensión por guardia

Como **guardia**, quiero **activar o suspender el permiso de una persona**, para **reaccionar de inmediato a una novedad operativa**.

**Criterios:** puedo alternar el estado desde el panel; una persona suspendida es denegada; el estado es visible junto a su perfil; el cambio persiste en la base.

## HU-05 — Suspensión por rango de fechas

Como **guardia**, quiero **definir una suspensión entre fechas**, para **cubrir sanciones, incapacidades o cambios temporales de turno**.

**Criterios:** el rango exige fecha final igual o posterior a la inicial; la suspensión aplica dentro del rango; fuera del rango el permiso puede evaluarse normalmente.

## HU-06 — Publicación del cronograma

Como **secretaría**, quiero **registrar medicamento, día, franja y actividad**, para **publicar el contexto operativo que usa el motor ABAC**.

**Criterios:** la franja exige medicamento y horas; la agenda publicada muestra día, horario, medicamento y tipo; una evaluación solo considera franjas activas.

## HU-07 — Tarea alternativa

Como **empleado con acceso denegado**, quiero **recibir una tarea alternativa**, para **continuar mi jornada sin ingresar al área restringida**.

**Criterios:** una denegación por cronograma, perfil o suspensión devuelve una tarea activa; el mensaje se muestra en el simulador; la tarea queda asociada al registro.

## HU-08 — Auditoría

Como **administrador**, quiero **consultar los intentos de acceso ordenados por fecha**, para **investigar incidentes y demostrar trazabilidad**.

**Criterios:** el listado incluye persona, acción, resultado, fecha, motivo y medicamento; los últimos registros aparecen primero; el endpoint permite filtrar por usuario.
