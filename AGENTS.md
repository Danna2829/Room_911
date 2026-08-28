# Reglas de Trabajo para Agentes (AGENTS.md) - Proyecto room_911

Bienvenido al proyecto **room_911** (Sistema de Control de Acceso Dinámico por Matriz de Riesgo y Cronograma).
Este documento establece las normas obligatorias, estándares de ingeniería y buenas prácticas que **todos los agentes AI y desarrolladores** deben cumplir sin excepción.

---

## 🚫 Reglas Restringidas / Prohibiciones Estrictas

1. **PROHIBIDO EL USO DE `npm`**:
   - Queda estrictamente prohibido ejecutar `npm install`, `npm run`, `npm test` o cualquier variante de `npm`.
   - **Única alternativa permitida**: `pnpm` (`pnpm install`, `pnpm dev`, `pnpm build`, `pnpm test`).
   - Todos los proyectos de frontend deben gestionar sus dependencias utilizando `pnpm` o workspaces de `pnpm`.

2. **PROHIBIDO EL USO DE HEREDOCS (`cat << EOF` y similares)**:
   - Queda prohibida la creación o modificación de archivos mediante heredocs de bash (`cat << 'EOF' > archivo`, `cat << EOF`, `tee << EOF`).
   - **Razón**: Los heredocs en scripts bash son propensos a errores de escape, truncamiento silencioso y dificultan la trazabilidad/linting.
   - **Alternativas adecuadas**:
     - Herramientas dedicadas de edición (`write_to_file`, `replace_file_content`).
     - Scripts auxiliares de Python/Node o editores de texto integrados.

---

## 📋 Reglas de Calidad y Estándares de Trabajo

### 1. Inspección Estricta de Logs y Errores
- **NUNCA** asumir o adivinar causas de fallas en ejecución o compilación.
- Inspeccionar el log completo de error antes de formular diagnósticos o proponer cambios.
- Realizar análisis de la causa raíz (*Root Cause Analysis*) apoyado en evidencia empírica.

### 2. Cero Patches Superficiales
- No silenciar excepciones con `try/catch` vacíos.
- No retornar valores 'dummy' o falsos para ocultar fallas de API o base de datos.
- No comentar pruebas unitarias ni deshabilitar aserciones para simular éxito.

### 3. Verificación de Cambios y Ejecución
- Ninguna tarea se considera completada sin ejecución previa de los comandos de verificación (ej. `mvn test`, `pnpm build`).
- Modificar un archivo no equivale a terminar la tarea; se debe comprobar empíricamente su correcto funcionamiento.

### 4. Trazabilidad Obligatoria (Handoffs)
- Todo trabajo realizado debe quedar documentado en `/home/fabrica/Documentos/Reto_911/HANDOFFS.md` registrando:
  - **Fecha**
  - **Qué se hizo**
  - **Cómo se hizo**
  - **Qué sigue / Pasos futuros**
  - **Contexto adecuado y detallado**

### 5. Formato Obligatorio de Resumen de Respuesta
- En cada respuesta al usuario, se DEBE incluir siempre un resumen final estructurado con las siguientes 3 secciones:
  - **Qué se hizo**: Resumen claro de los avances y modificaciones realizadas.
  - **Qué necesito de ti**: Preguntas, aclaraciones o insumos que el usuario deba proveer.
  - **Qué sigue**: Próximos pasos en la hoja de ruta del proyecto.

---

## 🛠️ Stack Tecnológico Permitido

- **Backend**: Java 17+ / Spring Boot 3.x, Spring Data JPA, PostgreSQL / H2, JUnit 5.
- **Frontend**: React / Vue / Angular (gestor de paquetes obligatorio: `pnpm`).
- **Control de Versiones y Modelado**: Git, Liquibase/Flyway para DDL de Base de Datos.
