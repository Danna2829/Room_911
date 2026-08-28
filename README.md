# Proyecto room_911 - Sistema de Control de Acceso Dinámico (ABAC / RBAC)

Sistema integral de gestión y control de acceso dinámico por Matriz de Riesgo y Cronograma Operativo para la sala restringida **room_911** de un laboratorio farmacéutico.

---

## 📌 Requisitos Previos y Reglas (`AGENTS.md`)
- **Backend**: Java 17+, Maven 3.x, PostgreSQL 13+.
- **Frontend**: Node.js v18+, **`pnpm` obligado** (`npm` está estrictamente prohibido por norma del proyecto).

---

## 🚀 Cómo Ejecutar el Proyecto

### 1. Inicialización de Base de Datos PostgreSQL
Asegúrese de tener PostgreSQL en ejecución en `localhost:5432` con la base de datos `Sala_911`:
```sql
CREATE DATABASE "Sala_911";
```
*(Los scripts `schema.sql` y `data.sql` se ejecutarán automáticamente al iniciar el Backend)*.

### 2. Ejecución del Backend (Spring Boot)
Navegar al directorio `/home/fabrica/Documentos/Reto_911/back`:
```bash
cd back
mvn spring-boot:run
```
- **Puerto**: `http://localhost:8080`
- **Pruebas unitarias**: `mvn test`

### 3. Ejecución del Frontend (React con `pnpm`)
Navegar al directorio `/home/fabrica/Documentos/Reto_911/Front/room911-frontend`:
```bash
cd Front/room911-frontend
pnpm install
pnpm start
```
- **Puerto**: `http://localhost:3000`
- **Build de producción**: `pnpm build`

---

## 👥 Cuentas de Prueba Pre-configuradas

| ID Expediente | Nombre | Rol | Nivel Operario (ABAC) | Contraseña | Permisos ABAC |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **EMP-0001** | Admin Sistema | `ADMINISTRADOR` | N/A | `admin123` | Gestión global y administración |
| **EMP-0002** | Carlos Guardia | `GUARDIA_SEGURIDAD` | N/A | `guardia123` | Control de suspensiones y monitor |
| **EMP-8821** | Juan Operario 1 | `OPERARIO` | **Nivel 1** | `operario123` | Acceso a Medicamentos Tipo 1 y 2 |
| **EMP-8822** | Maria Operario 2 | `OPERARIO` | **Nivel 2** | `operario123` | Acceso a Medicamentos Tipo 2 y 5 |
| **EMP-8823** | Pedro Operario 3 | `OPERARIO` | **Nivel 3** | `operario123` | **Acceso Global** (Incluye Tipo 4 Restringido) |

---

## 🔌 Principales Endpoints REST (API Backend)

- **Torniquete Garita**: `POST /api/acceso/evaluar` (`{ "idUsuario": "EMP-8821", "tipoEvento": "ENTRADA" }`)
- **Monitor en Vivo**: `GET /api/acceso/monitor`
- **Secretaría (Cronograma)**: `POST /api/cronograma`, `GET /api/cronograma/hoy`
- **Guardia (Suspensiones)**: `POST /api/guardia/suspender`, `GET /api/guardia/suspensiones`
- **Administración Usuarios**: `POST /api/admin/crear-usuario`, `GET /api/admin/listar-usuarios`
- **Inventario**: `GET /api/inventario`, `POST /api/inventario/entrada`
- **Exportar Reportes**: `GET /api/reportes/exportar/csv`

---

## 📑 Documentación de Apoyo
- `DOMINIO.md`: Modelo relacional y diagramas del reto.
- `AUDITORIA_DB.md`: Informe de auditoría de BD y scripts DDL.
- `HISTORIAS_DE_USUARIO.md`: Fichas detalladas de las 28 HUs.
- `PLAN_DE_ACCION.md`: Cronograma por fases del sprint de 15 días.
- `HANDOFFS.md`: Bitácora de trazabilidad e historial continuo.
