# API y reglas de negocio

Base URL: `http://localhost:8080/api`.

| Método | Ruta | Uso |
|---|---|---|
| GET | `/salud` | Verificar disponibilidad. |
| POST | `/usuarios/identificar` | Identificar por `{ "idUsuario": "EMP-301" }`; devuelve rol y nivel. |
| GET | `/usuarios` | Listar perfiles operativos. |
| PATCH | `/usuarios/{id}/estado?activo=false&desde=2026-08-31&hasta=2026-09-02` | Activar/suspender. |
| GET | `/medicamentos` | Catálogo. |
| GET | `/cronogramas` | Agenda publicada. |
| POST | `/cronogramas` | Crear `{diaSemana,horaInicio,horaFin,medicamentoId,actividad}`. |
| PUT | `/cronogramas/{id}` | Actualizar una franja publicada con el mismo payload. |
| GET | `/accesos/filtrar?resultado=DENEGADO&idUsuario=EMP-301&desde=2026-08-31&hasta=2026-09-01` | Filtrar auditoría por resultado, usuario y rango de fechas. |
| GET | `/accesos/exportar.csv` | Descargar la auditoría en CSV; acepta los mismos filtros. |
| POST | `/accesos/evaluar` | Ejecutar el motor y registrar el intento. |
| GET | `/accesos` | Auditoría ordenada desde el más reciente. |
| GET | `/accesos/usuario/{id}` | Auditoría de una persona. |

## Solicitud de evaluación

```json
{
  "idUsuario": "EMP-301",
  "accion": "ENTRADA",
  "medicamentoId": "M001",
  "fechaHora": "2026-08-31T10:00:00",
  "direccionIP": "127.0.0.1"
}
```

El campo `fechaHora` es opcional; omitirlo usa la hora del servidor. La respuesta devuelve `permitido`, `resultado`, `motivo`, `tareaAlternativa`, medicamento y timestamp.

## Matriz de decisión

1. El ID debe existir.
2. El usuario debe estar activo y fuera de cualquier rango de suspensión.
3. Debe existir una franja del día y hora actuales.
4. Si se envía medicamento, debe coincidir con la franja; si no se envía, se toma el de la franja activa.
5. El perfil debe permitir el tipo: Nivel 1 = 1/2, Nivel 2 = 2/5, Nivel 3 = global.
6. Si una condición falla, se deniega, se asigna la primera tarea alternativa activa y se registra el intento.
