# Especificación Completa y Exhaustiva de Historias de Usuario (HU) - Proyecto room_911

**Documento Origen**: `HU 3197815(1).docx` (Preservado intacto)  
**Diagramas Base**: `Room_911-Página-1.drawio.png` a `Room_911-Página-6.drawio.png`  
**Especificación Técnica**: `Reto_room_911_Extendido-1.pdf`  

---

# 👤 MÓDULO 1: GESTIONAR USUARIOS Y AUTENTICACIÓN

---

## 📋 HISTORIA DE USUARIO: HU-001

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-001** |
| **Nombre** | Iniciar sesión |
| **Complejidad** | Media |
| **HU Relacionada** | N/A |
| **Módulo** | Gestionar Usuarios |
| **Descripción** | **Yo como**: Administrador, Operario y Guardia de seguridad<br/>**Requiero**: Ingresar al sistema mediante mi correo electrónico y contraseña o ID interno<br/>**Para**: Acceder a las funcionalidades correspondientes a mi rol |
| **Requerimiento** | El sistema debe mostrar una pantalla de inicio de sesión con campos para correo electrónico y contraseña. Al validar las credenciales, redirigirá al dashboard según el rol del usuario (Administrador, Guardia, Secretaría, Operario). Si la cuenta está inactiva o la contraseña es errónea, mostrará los mensajes de error correspondientes. |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Credenciales válidas**:
  * **Dado**: Que el usuario ingrese correo y contraseña válidos en el formulario.
  * **Cuando**: Presione el botón "Iniciar Sesión".
  * **Entonces**: El sistema autenticará al usuario, generará un token JWT y redirigirá al panel correspondiente a su rol.
* **Condición 02 - Credenciales inválidas**:
  * **Dado**: Que el usuario ingrese un correo o contraseña incorrectos.
  * **Cuando**: Presione "Iniciar Sesión".
  * **Entonces**: El sistema mostrará el mensaje "Credenciales inválidas. Verifique sus datos".
* **Condición 03 - Campos vacíos**:
  * **Dado**: Que el usuario omita el correo o la contraseña.
  * **Cuando**: Intente enviar el formulario.
  * **Entonces**: El sistema resaltará los campos en rojo y mostrará "Los campos son obligatorios".
* **Condición 04 - Cuenta inactiva**:
  * **Dado**: Que un usuario inactivo intente iniciar sesión con credenciales correctas.
  * **Cuando**: Envíe el formulario.
  * **Entonces**: El sistema mostrará "Su cuenta se encuentra inactiva. Contacte al Administrador".

### TAREAS
1. Diseñar vista de Login responsive.
2. Implementar validación de campos obligatorios en el frontend.
3. Crear endpoint POST `/api/auth/login` en Spring Boot.
4. Implementar encriptación de contraseña con BCrypt.
5. Generar token JWT con claims de ID, Rol y Nivel.
6. Guardar estado de sesión en frontend.
7. Implementar redirección por rol (Admin, Guardia, Secretaría, Operario).
8. Registrar intento de inicio de sesión en auditoría.

### CONTROL DE VERSIONES
| Versión | Fecha | Autor | Revisión | Descripción | Aprobador |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1.0 | 2026-08-28 | Danna Arévalo | 1.0 | Creación inicial de la HU | Equipo room_911 |

---

## 📋 HISTORIA DE USUARIO: HU-002

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-002** |
| **Nombre** | Restablecer contraseña |
| **Complejidad** | Media |
| **HU Relacionada** | HU-001 |
| **Módulo** | Gestionar Usuarios |
| **Descripción** | **Yo como**: Administrador, Operario y Guardia de seguridad<br/>**Requiero**: Restablecer mi contraseña en caso de olvido<br/>**Para**: Recuperar el acceso seguro a mi cuenta |
| **Requerimiento** | El sistema debe permitir ingresar el correo electrónico registrado para solicitar un código/enlace de recuperación. Posteriormente permitirá ingresar una nueva contraseña cumpliendo las políticas de seguridad. |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Solicitud de recuperación**:
  * **Dado**: Que el usuario ingrese su correo electrónico registrado.
  * **Cuando**: Presione "Enviar enlace de recuperación".
  * **Entonces**: El sistema enviará/generará un token temporal de recuperación y mostrará "Enlace enviado a su correo".
* **Condición 02 - Correo no registrado**:
  * **Dado**: Que el usuario ingrese un correo no registrado en la BD.
  * **Cuando**: Presione "Enviar enlace".
  * **Entonces**: El sistema mostrará "No existe una cuenta asociada a este correo".
* **Condición 03 - Requisitos de contraseña**:
  * **Dado**: Que el usuario ingrese la nueva contraseña.
  * **Cuando**: Intente guardarla.
  * **Entonces**: El sistema validará que contenga mínimo 8 caracteres, al menos una mayúscula y un número.

### TAREAS
1. Diseñar pantalla de solicitud de recuperación de contraseña.
2. Diseñar pantalla de cambio de contraseña.
3. Crear endpoint POST `/api/auth/recuperar-contrasena`.
4. Validar token temporal de recuperación.
5. Encriptar y actualizar contraseña en BD.
6. Registrar evento en historial de auditoría.

### CONTROL DE VERSIONES
| Versión | Fecha | Autor | Revisión | Descripción | Aprobador |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1.0 | 2026-08-28 | Danna Arévalo | 1.0 | Creación inicial de la HU | Equipo room_911 |

---

## 📋 HISTORIA DE USUARIO: HU-003

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-003** |
| **Nombre** | Registrar Usuario |
| **Complejidad** | Alta |
| **HU Relacionada** | HU-001, HU-002 |
| **Módulo** | Gestionar Usuarios |
| **Descripción** | **Yo como**: Administrador del sistema<br/>**Requiero**: Registrar nuevos usuarios en el sistema (Operarios, Administrador y Guardias)<br/>**Para**: Garantizar que solo el personal autorizado tenga acceso |
| **Requerimiento** | El administrador podrá diligenciar un formulario con Nombre, Apellido, Correo, Rol, Nivel de Operario (si aplica) y Contraseña. El sistema generará automáticamente un ID único de expediente interno (ej. `EMP-8821`). Validará correo único y contraseña segura. |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Campos obligatorios**:
  * **Dado**: Que el administrador esté en el formulario.
  * **Cuando**: Omita ingresar algún campo requerido.
  * **Entonces**: El sistema mostrará "Todos los campos marcados son obligatorios".
* **Condición 02 - Formato de correo válido**:
  * **Dado**: Que el administrador ingrese un correo sin `@` o dominio válido.
  * **Cuando**: Presione "Registrar".
  * **Entonces**: El sistema mostrará "Formato de correo electrónico inválido".
* **Condición 03 - Duplicidad de correo**:
  * **Dado**: Que el correo ingresado ya pertenezca a otro usuario.
  * **Cuando**: Se envíe el formulario.
  * **Entonces**: El sistema mostrará "El correo electrónico ya se encuentra registrado".
* **Condición 04 - Generación automática de ID Interno**:
  * **Dado**: Que los datos sean válidos.
  * **Cuando**: Se confirme el registro.
  * **Entonces**: El sistema generará automáticamente un ID único con formato `EMP-XXXX` (ej. `EMP-8821`).
* **Condición 05 - Auditoría de registro**:
  * **Dado**: Que el usuario sea registrado exitosamente.
  * **Cuando**: Se guarde en BD.
  * **Entonces**: Se registrará fecha, hora e ID del administrador responsable en la tabla de auditoría.

### TAREAS
1. Diseñar interfaz gráfica del formulario de registro de usuario.
2. Implementar selector de rol (`ADMINISTRADOR`, `GUARDIA_SEGURIDAD`, `OPERARIO`, `SECRETARIA`).
3. Implementar selector de nivel de operario (Nivel 1, 2, 3).
4. Implementar generador de ID `EMP-XXXX` en `UsuarioService.java`.
5. Validar unicidad de correo en BD.
6. Encriptar contraseña antes de persistir.
7. Guardar perfil de operario en `perfiles_operario`.
8. Registrar evento en `registros_auditoria`.

### CONTROL DE VERSIONES
| Versión | Fecha | Autor | Revisión | Descripción | Aprobador |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1.0 | 2026-08-28 | Danna Arévalo | 1.0 | Creación inicial de la HU | Equipo room_911 |

---

## 📋 HISTORIA DE USUARIO: HU-004

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-004** |
| **Nombre** | Consultar usuario |
| **Complejidad** | Media |
| **HU Relacionada** | HU-003 |
| **Módulo** | Gestionar Usuarios |
| **Descripción** | **Yo como**: Administrador, Operario y Guardia<br/>**Requiero**: Consultar la información de mi usuario o de los usuarios registrados<br/>**Para**: Visualizar datos de acceso y roles según mis funciones |
| **Requerimiento** | Se mostrará una tabla con ID Expediente, Nombre, Correo, Rol y Estado. El Administrador puede consultar todos y filtrar. El Guardia solo ve ID, Rol y Estado. El Operario solo ve su propio perfil. |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Listado general (Admin)**:
  * **Dado**: Que el Administrador acceda a la vista de usuarios.
  * **Cuando**: Existan usuarios registrados.
  * **Entonces**: El sistema mostrará la tabla completa con opciones de búsqueda y filtrado.
* **Condición 02 - Sin usuarios registrados**:
  * **Dado**: Que no existan registros en la BD.
  * **Cuando**: Se acceda a la pantalla.
  * **Entonces**: El sistema mostrará "No existen usuarios registrados".
* **Condición 03 - Consulta por ID o filtro**:
  * **Dado**: Que se busque un ID que no exista.
  * **Cuando**: Se ejecute la búsqueda.
  * **Entonces**: El sistema mostrará "Usuario no encontrado".
* **Condición 04 - Visualización limitada Guardia**:
  * **Dado**: Que el Guardia acceda a la consulta.
  * **Cuando**: Seleccione un usuario.
  * **Entonces**: El sistema mostrará únicamente ID, Rol y Estado, ocultando el correo y contraseña.

### TAREAS
1. Diseñar tabla interactiva de usuarios en React.
2. Implementar barra de búsqueda y filtros.
3. Crear endpoint GET `/api/admin/listar-usuarios`.
4. Aplicar DTOs con restricciones de visibilidad según el rol.

### CONTROL DE VERSIONES
| Versión | Fecha | Autor | Revisión | Descripción | Aprobador |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1.0 | 2026-08-28 | Danna Arévalo | 1.0 | Creación inicial de la HU | Equipo room_911 |

---

## 📋 HISTORIA DE USUARIO: HU-005

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-005** |
| **Nombre** | Asignar Rol de Usuario |
| **Complejidad** | Media |
| **HU Relacionada** | HU-003, HU-004 |
| **Módulo** | Gestionar Usuarios |
| **Origen Diagrama** | `Room_911-Página-1.drawio.png` (CU005) |
| **Descripción** | **Yo como**: Administrador del sistema<br/>**Requiero**: Asignar o cambiar el rol de un usuario existente<br/>**Para**: Otorgar las facultades operativas o de seguridad correspondientes |
| **Requerimiento** | El Administrador podrá seleccionar un usuario y actualizar su rol (`ADMINISTRADOR`, `GUARDIA_SEGURIDAD`, `OPERARIO`, `SECRETARIA`). El cambio aplicará de forma inmediata en las políticas de seguridad. |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Cambio de rol exitoso**:
  * **Dado**: Que el Administrador seleccione un nuevo rol válido para un usuario.
  * **Cuando**: Presione "Guardar Rol".
  * **Entonces**: El sistema actualizará el rol en BD y mostrará "Rol asignado correctamente".
* **Condición 02 - Confirmación de cambio crítico**:
  * **Dado**: Que se cambie el rol de un usuario a Administrador o Guardia.
  * **Cuando**: Se envíe la modificación.
  * **Entonces**: El sistema solicitará confirmación previa.

### TAREAS
1. Crear modal de asignación de rol en frontend.
2. Implementar endpoint PUT `/api/admin/editar-usuario/{id}`.
3. Registrar modificación en auditoría.

### CONTROL DE VERSIONES
| Versión | Fecha | Autor | Revisión | Descripción | Aprobador |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1.0 | 2026-08-28 | Equipo room_911 | 1.0 | Creación según diagramas Draw.io | Equipo room_911 |

---

## 📋 HISTORIA DE USUARIO: HU-006

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-006** |
| **Nombre** | Editar Usuario |
| **Complejidad** | Media |
| **HU Relacionada** | HU-004 |
| **Módulo** | Gestionar Usuarios |
| **Origen Diagrama** | `Room_911-Página-1.drawio.png` (CU007) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Modificar los datos personales de un usuario (Nombre, Apellido, Correo)<br/>**Para**: Mantener la información del personal actualizada |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Edición de datos válida**:
  * **Dado**: Que se modifiquen los datos de un usuario existente con valores válidos.
  * **Cuando**: Se presione "Actualizar".
  * **Entonces**: El sistema guardará los cambios y mostrará "Usuario actualizado correctamente".

### TAREAS
1. Implementar vista/modal de edición.
2. Conectar endpoint PUT `/api/admin/editar-usuario/{id}`.

---

## 📋 HISTORIA DE USUARIO: HU-007

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-007** |
| **Nombre** | Activar / Desactivar Usuario |
| **Complejidad** | Baja |
| **HU Relacionada** | HU-004 |
| **Módulo** | Gestionar Usuarios |
| **Origen Diagrama** | `Room_911-Página-1.drawio.png` (CU008) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Cambiar el estado de un usuario entre `Activo` e `Inactivo`<br/>**Para**: Impedir o restituir su acceso general al sistema |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Desactivación de usuario**:
  * **Dado**: Que un usuario esté activo.
  * **Cuando**: El administrador presione "Desactivar".
  * **Entonces**: El estado cambiará a `Inactivo` y se le impedirá el inicio de sesión o entrada a room_911.

---

## 📋 HISTORIA DE USUARIO: HU-008

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-008** |
| **Nombre** | Suspender / Reactivar Permisos por Guardia |
| **Complejidad** | Alta |
| **HU Relacionada** | HU-004 |
| **Módulo** | Control de Seguridad |
| **Origen Diagrama** | `Room_911-Página-1.drawio.png` (CU010) & PDF Sección 1 |
| **Descripción** | **Yo como**: Guardia de seguridad<br/>**Requiero**: Suspender o reactivar permisos individuales de forma inmediata o por rango de fechas (por incapacidad, sanción o cambio de turno)<br/>**Para**: Bloquear de inmediato el acceso físico a room_911 ante cualquier eventualidad |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Suspensión inmediata**:
  * **Dado**: Que el Guardia ingrese el ID del operario y el motivo (`INCAPACIDAD`, `SANCIÓN`, `CAMBIO_TURNO`).
  * **Cuando**: Presione "Suspender Permisos".
  * **Entonces**: El sistema registrará la suspensión y denegará automáticamente cualquier intento de entrada a room_911.
* **Condición 02 - Reactivación de permisos**:
  * **Dado**: Que exista una suspensión activa.
  * **Cuando**: El Guardia presione "Reactivar".
  * **Entonces**: La suspensión quedará inactiva y el operario recuperará sus permisos.

### TAREAS
1. Diseñar formulario de suspensión en `PanelGuardia.js`.
2. Crear endpoint POST `/api/guardia/suspender`.
3. Crear endpoint PUT `/api/guardia/suspensiones/{id}/desactivar`.
4. Evaluar suspensiones activas en el motor ABAC.

---

## 📋 HISTORIA DE USUARIO: HU-009

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-009** |
| **Nombre** | Cerrar sesión |
| **Complejidad** | Baja |
| **Módulo** | Gestionar Usuarios |
| **Origen Diagrama** | `Room_911-Página-1.drawio.png` (CU011) |
| **Descripción** | **Yo como**: Usuario autenticado<br/>**Requiero**: Cerrar mi sesión de trabajo<br/>**Para**: Proteger el acceso a la cuenta en terminales compartidas |

---

# 🔐 MÓDULO 2: CONTROL DE PERMISOS Y ACCESOS (ABAC & GARITA)

---

## 📋 HISTORIA DE USUARIO: HU-010

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-010** |
| **Nombre** | Asignar Nivel de Acceso a Operario |
| **Complejidad** | Media |
| **HU Relacionada** | HU-003 |
| **Módulo** | Permisos y Accesos |
| **Origen Diagrama** | `Room_911-Página-2.drawio.png` (CU001) & PDF Sección 2 |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Asignar a cada operario un Nivel de Acceso (Nivel 1, Nivel 2, Nivel 3)<br/>**Para**: Determinar qué categorías de medicamentos puede manipular según la matriz ABAC |
| **Requerimiento** | Se asignará Nivel 1 (Medicamentos Tipo 1 y 2), Nivel 2 (Tipo 2 y 5) o Nivel 3 (Acceso Global, incluyendo Tipo 4 Restringido). |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Asignación de Nivel**:
  * **Dado**: Que se seleccione un operario y su nivel de acceso.
  * **Cuando**: Se guarde en el sistema.
  * **Entonces**: Se persistirá en `perfiles_operario` y se aplicará inmediatamente en la garita.

---

## 📋 HISTORIA DE USUARIO: HU-011

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-011** |
| **Nombre** | Monitorear Intentos de Acceso en Tiempo Real |
| **Complejidad** | Alta |
| **Módulo** | Control de Seguridad |
| **Origen Diagrama** | `Room_911-Página-2.drawio.png` (CU004) & PDF Sección 1 |
| **Descripción** | **Yo como**: Guardia de seguridad<br/>**Requiero**: Visualizar en tiempo real el flujo de intentos de acceso (ENTRADA / SALIDA) a room_911<br/>**Para**: Supervisar los eventos de seguridad y denegaciones al instante |

---

## 📋 HISTORIA DE USUARIO: HU-012

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-012** |
| **Nombre** | Validar Acceso Dinámico en Garita / Torniquete Táctil |
| **Complejidad** | Muy Alta |
| **Módulo** | Permisos y Accesos |
| **Origen Diagrama** | `Room_911-Página-2.drawio.png` (CU001/CU002 Operario) & PDF Sección 4 |
| **Descripción** | **Yo como**: Operario<br/>**Requiero**: Ingresar únicamente mi ID Interno (ej. `EMP-8821`) en el simulador de garita para marcar ENTRADA o SALIDA<br/>**Para**: Obtener la autorización física de ingreso a room_911 en milisegundos |
| **Requerimiento** | El motor ABAC evaluará: 1) Si existe el expediente y el usuario está activo. 2) Si no existe una suspensión por Guardia. 3) Si la categoría de medicamento programada en el día coincide con los permisos del Nivel del Operario. |

### CRITERIOS DE ACEPTACIÓN
* **Condición 01 - Acceso Permitido**:
  * **Dado**: Que un Operario Nivel 3 (o nivel compatible) ingrese su ID en un día programado.
  * **Cuando**: Presione "Simular ENTRADA".
  * **Entonces**: El sistema mostrará la pantalla verde "PERMITIDO", registrará el evento y abrirá el torniquete.
* **Condición 02 - Denegado por Suspensión**:
  * **Dado**: Que el operario tenga una suspensión activa por la Guardia.
  * **Cuando**: Marque su entrada.
  * **Entonces**: El sistema mostrará la pantalla roja "DENEGADO" con el motivo de la suspensión.
* **Condición 03 - Denegado por Cronograma**:
  * **Dado**: Que un Operario Nivel 1 intente ingresar un día que room_911 procese Medicamento Tipo 4.
  * **Cuando**: Presione "ENTRADA".
  * **Entonces**: El sistema denegará la entrada y activará el Plan de Contingencia (HU-013).

---

## 📋 HISTORIA DE USUARIO: HU-013

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-013** |
| **Nombre** | Redirección Automática de Tareas (Plan de Contingencia) |
| **Complejidad** | Alta |
| **Módulo** | Permisos y Accesos |
| **Origen Diagrama** | `Room_911-Página-2.drawio.png` (CU004 Operario) & PDF Sección 3 |
| **Descripción** | **Yo como**: Operario denegado por cronograma<br/>**Requiero**: Recibir en la pantalla de garita una asignación alternativa automática (ej. *"Acceso denegado: Asignado a investigación en Lab-B"* o *"Atención a clientes"*<br/>**Para**: Conocer mi nueva actividad asignada para la jornada sin quedar desocupado |

---

# 📅 MÓDULO 3: GESTIONAR CRONOGRAMA OPERATIVO (SECRETARÍA)

---

## 📋 HISTORIA DE USUARIO: HU-014

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-014** |
| **Nombre** | Crear Cronograma Operativo Diario |
| **Complejidad** | Media |
| **Módulo** | Cronograma Operativo |
| **Origen Diagrama** | `Room_911-Página-3.drawio.png` (CU001) & PDF Sección 3 |
| **Descripción** | **Yo como**: Secretaría / Administrador<br/>**Requiero**: Programar por fecha qué categoría de medicamento (ej. Tipo 4 Restringido) se procesará en room_911<br/>**Para**: Establecer la regla dinámica de acceso que evaluará la garita ese día |

---

## 📋 HISTORIA DE USUARIO: HU-015

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-015** |
| **Nombre** | Editar Cronograma Operativo |
| **Complejidad** | Media |
| **Módulo** | Cronograma Operativo |
| **Origen Diagrama** | `Room_911-Página-3.drawio.png` (CU002) |
| **Descripción** | **Yo como**: Secretaría<br/>**Requiero**: Modificar la categoría programada para una fecha futura en caso de cambios en producción |

---

## 📋 HISTORIA DE USUARIO: HU-016

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-016** |
| **Nombre** | Consultar Cronograma Operativo |
| **Complejidad** | Baja |
| **Módulo** | Cronograma Operativo |
| **Origen Diagrama** | `Room_911-Página-3.drawio.png` (CU003) |
| **Descripción** | **Yo como**: Guardia, Operario y Administrador<br/>**Requiero**: Consultar el calendario de actividades de room_911 para conocer qué sustancia está programada hoy |

---

# 💊 MÓDULO 4: GESTIONAR CATEGORÍA DE MEDICAMENTOS

---

## 📋 HISTORIA DE USUARIO: HU-017

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-017** |
| **Nombre** | Crear Categoría de Medicamento |
| **Complejidad** | Media |
| **Módulo** | Categoría Medicamentos |
| **Origen Diagrama** | `Room_911-Página-4.drawio.png` (CU001) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Registrar nuevas categorías de medicamentos (Tipo 1, Tipo 2, Tipo 3, Tipo 4 Restringido, Tipo 5)<br/>**Para**: Categorizar el riesgo de las sustancias manipuladas |

---

## 📋 HISTORIA DE USUARIO: HU-018

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-018** |
| **Nombre** | Editar Categoría de Medicamento |
| **Complejidad** | Media |
| **Módulo** | Categoría Medicamentos |
| **Origen Diagrama** | `Room_911-Página-4.drawio.png` (CU002) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Actualizar el nombre, código o nivel de restricción de una categoría |

---

## 📋 HISTORIA DE USUARIO: HU-019

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-019** |
| **Nombre** | Consultar Categorías de Medicamento |
| **Complejidad** | Baja |
| **Módulo** | Categoría Medicamentos |
| **Origen Diagrama** | `Room_911-Página-4.drawio.png` (CU003) |
| **Descripción** | **Yo como**: Usuario del sistema<br/>**Requiero**: Visualizar la lista de categorías registradas en el laboratorio |

---

## 📋 HISTORIA DE USUARIO: HU-020

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-020** |
| **Nombre** | Activar / Deshabilitar Categoría |
| **Complejidad** | Baja |
| **Módulo** | Categoría Medicamentos |
| **Origen Diagrama** | `Room_911-Página-4.drawio.png` (CU004) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Inhabilitar temporalmente una categoría obsoleta o sin producción |

---

# 📦 MÓDULO 5: GESTIONAR INVENTARIO DE MEDICAMENTOS

---

## 📋 HISTORIA DE USUARIO: HU-021

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-021** |
| **Nombre** | Registrar Entrada de Inventario |
| **Complejidad** | Media |
| **Módulo** | Inventario |
| **Origen Diagrama** | `Room_911-Página-5.drawio.png` (CU001) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Registrar el ingreso de lotes de medicamentos a room_911<br/>**Para**: Mantener el control de existencias físicas |

---

## 📋 HISTORIA DE USUARIO: HU-022

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-022** |
| **Nombre** | Registrar Salida de Inventario |
| **Complejidad** | Media |
| **Módulo** | Inventario |
| **Origen Diagrama** | `Room_911-Página-5.drawio.png` (CU002) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Registrar el egreso o uso de lotes de medicamentos |

---

## 📋 HISTORIA DE USUARIO: HU-023

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-023** |
| **Nombre** | Validar Stock de Medicamentos |
| **Complejidad** | Media |
| **Módulo** | Inventario |
| **Origen Diagrama** | `Room_911-Página-5.drawio.png` (CU004) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Verificar el stock disponible y recibir alertas de stock mínimo |

---

## 📋 HISTORIA DE USUARIO: HU-024

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-024** |
| **Nombre** | Consultar Inventario de Medicamentos |
| **Complejidad** | Baja |
| **Módulo** | Inventario |
| **Origen Diagrama** | `Room_911-Página-5.drawio.png` (CU001 Operario / CU003 Guardia) |
| **Descripción** | **Yo como**: Operario y Guardia<br/>**Requiero**: Consultar la lista de sustancias en stock en room_911 |

---

# 📊 MÓDULO 6: REPORTES Y AUDITORÍA DE SEGURIDAD

---

## 📋 HISTORIA DE USUARIO: HU-025

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-025** |
| **Nombre** | Generar Reportes de Acceso |
| **Complejidad** | Alta |
| **Módulo** | Reportes y Auditoría |
| **Origen Diagrama** | `Room_911-Página-6.drawio.png` (CU001) & PDF Roadmap Días 12-15 |
| **Descripción** | **Yo como**: Administrador y Guardia<br/>**Requiero**: Generar reportes consolidados de intentos de acceso (PERMITIDO/DENEGADO) por rango de fechas<br/>**Para**: Auditar los patrones de ingreso a la sala de alto control |

---

## 📋 HISTORIA DE USUARIO: HU-026

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-026** |
| **Nombre** | Generar Reportes de Inventario |
| **Complejidad** | Media |
| **Módulo** | Reportes y Auditoría |
| **Origen Diagrama** | `Room_911-Página-6.drawio.png` (CU002) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Generar reportes de entradas y salidas de stock de medicamentos |

---

## 📋 HISTORIA DE USUARIO: HU-027

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-027** |
| **Nombre** | Consultar y Exportar Reportes (PDF / Excel / CSV) |
| **Complejidad** | Alta |
| **Módulo** | Reportes y Auditoría |
| **Origen Diagrama** | `Room_911-Página-6.drawio.png` (CU003/CU004) |
| **Descripción** | **Yo como**: Administrador<br/>**Requiero**: Consultar reportes en pantalla y exportarlos a archivos PDF, Excel o CSV<br/>**Para**: Presentar informes a auditorías externas y entes reguladores farmacéuticos |

---

## 📋 HISTORIA DE USUARIO: HU-028

| Campo | Detalle |
| :--- | :--- |
| **Código** | **HU-028** |
| **Nombre** | Consultar Historial de Auditoría de Seguridad |
| **Complejidad** | Alta |
| **Módulo** | Reportes y Auditoría |
| **Origen Diagrama** | PDF Sección 4 & Días 12-15 |
| **Descripción** | **Yo como**: Administrador del sistema<br/>**Requiero**: Auditar la traza inmutable de todos los eventos del sistema (quién modificó un permiso, quién suspendió a un usuario, fecha, hora e IP)<br/>**Para**: Garantizar el no repudio y la seguridad total del laboratorio farmacéutico |

---
