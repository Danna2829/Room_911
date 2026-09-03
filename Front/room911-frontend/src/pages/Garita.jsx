import { useState } from "react";
import api from "../api/api";
import { PageHeader } from "../components/ui/PageHeader";
import { Button } from "../components/ui/Button";
import { Icon } from "../components/ui/Icon";
import { Alert } from "../components/ui/Alert";
import { StatusPill, Spinner } from "../components/ui/feedback";
import { TextField, SelectField } from "../components/ui/inputs";
import { DataTable } from "../components/ui/DataTable";
import { useToast } from "../components/ui/Toast";
import { useAuth } from "../auth/AuthContext";
import { fmtDateTime } from "../utils/format";

const MAX_INTENTOS = 3;

export default function Garita() {
  const toast = useToast();
  const { user } = useAuth();
  const esOperario = user?.rol === "OPERARIO";

  const [idUsuario, setIdUsuario] = useState(esOperario ? (user?.idUsuario || "") : "");
  const [tipoEvento, setTipoEvento] = useState("ENTRADA");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [history, setHistory] = useState([]);

  // Modo operario: validacion del ID propio, nivel/medicamentos y plan de contingencia.
  const [intentosFallidos, setIntentosFallidos] = useState(0);
  const [tareaAlternativa, setTareaAlternativa] = useState("");
  const [perfil, setPerfil] = useState(null);
  const [perfilLoading, setPerfilLoading] = useState(false);

  const cargarPerfil = async (id) => {
    setPerfilLoading(true);
    try {
      const { data } = await api.get(`/acceso/perfil/${id}`);
      setPerfil(data);
    } catch {
      setPerfil(null);
    } finally {
      setPerfilLoading(false);
    }
  };

  const evaluar = async (e) => {
    e.preventDefault();
    const idIngresado = idUsuario.trim();
    if (!idIngresado) {
      toast.push({ type: "danger", title: "Datos incompletos", message: "Ingresa el ID de usuario." });
      return;
    }
    // El operario solo puede validar el expediente que le corresponde.
    if (esOperario && idIngresado.toUpperCase() !== (user?.idUsuario || "").toUpperCase()) {
      toast.push({
        type: "danger",
        title: "Expediente incorrecto",
        message: "Solo puedes validar tu propio ID interno (" + user.idUsuario + ").",
      });
      setResult(null);
      return;
    }

    setLoading(true);
    try {
      const { data } = await api.post("/acceso/evaluar", { idUsuario: idIngresado, tipoEvento });
      setResult(data);
      setHistory((h) => [data, ...h].slice(0, 10));
      toast.push({
        type: data.permitido ? "success" : "danger",
        title: data.permitido ? "Acceso permitido" : "Acceso denegado",
        message: data.mensaje,
      });

      if (esOperario && tipoEvento === "ENTRADA") {
        if (data.permitido) {
          setIntentosFallidos(0);
          setTareaAlternativa("");
          // Acceso correcto: mostrar nivel y medicamentos que va a manipular hoy.
          cargarPerfil(idIngresado);
        } else {
          const nuevos = intentosFallidos + 1;
          setIntentosFallidos(nuevos);
          if (nuevos >= MAX_INTENTOS) {
            try {
              const { data: alt } = await api.get("/acceso/tarea-alternativa");
              setTareaAlternativa(alt.tareaAlternativa || data.tareaAlternativa || "");
            } catch {
              setTareaAlternativa(data.tareaAlternativa || "Asignado a atención a clientes.");
            }
          }
        }
      }
    } catch {
      toast.push({ type: "danger", title: "Error", message: "No se pudo evaluar el acceso." });
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    { key: "idUsuario", label: "Operario", sortable: true },
    { key: "timestamp", label: "Fecha/Hora", sortable: true, render: (r) => fmtDateTime(r.timestamp) },
    {
      key: "resultado",
      label: "Resultado",
      sortable: true,
      render: (r) => <StatusPill tone={r.resultado === "PERMITIDO" ? "success" : "danger"}>{r.resultado}</StatusPill>,
    },
    { key: "mensaje", label: "Mensaje" },
  ];

  const bloqueadoPorIntentos = esOperario && intentosFallidos >= MAX_INTENTOS;

  return (
    <>
      <PageHeader
        title={esOperario ? "Torniquete room_911" : "Garita / Torniquete"}
        subtitle={
          esOperario
            ? "Valida tu expediente interno para registrar tu ENTRADA o SALIDA"
            : "Simulador táctil de evaluación de acceso (motor ABAC)"
        }
      />
      <div className="row g-4">
        <div className="col-12 col-lg-5">
          <div className="card">
            <div className="card-body">
              <h5 className="mb-3">
                <Icon name="door-closed" className="me-2" />
                {esOperario ? "Validar expediente" : "Evaluar acceso"}
              </h5>
              {esOperario && (
                <Alert variant="info">
                  <Icon name="person-badge" className="me-1" /> Tu expediente: <strong>{user?.idUsuario}</strong>
                </Alert>
              )}
              <form onSubmit={evaluar}>
                <TextField
                  id="idU"
                  label="ID de usuario"
                  icon="person-badge"
                  placeholder="EMP-8821"
                  value={idUsuario}
                  disabled={esOperario}
                  onChange={(e) => setIdUsuario(e.target.value)}
                />
                <SelectField
                  id="ev"
                  label="Tipo de evento"
                  value={tipoEvento}
                  onChange={(e) => setTipoEvento(e.target.value)}
                  options={[
                    { value: "ENTRADA", label: "ENTRADA" },
                    { value: "SALIDA", label: "SALIDA" },
                  ]}
                />
                <Button type="submit" block loading={loading} icon="shield-check">
                  {bloqueadoPorIntentos ? "Registrar intento" : "Evaluar acceso"}
                </Button>
              </form>
              {esOperario && intentosFallidos > 0 && !bloqueadoPorIntentos && (
                <p className="small text-muted-2 mt-2 mb-0">
                  Intentos fallidos: <strong>{intentosFallidos}</strong> de {MAX_INTENTOS}. Al tercero serás redirigido a tu tarea alternativa.
                </p>
              )}
            </div>
          </div>

          {result && (
            <div className="card mt-4">
              <div className="card-body">
                <Alert
                  variant={result.permitido ? "success" : "danger"}
                  title={result.permitido ? "Acceso PERMITIDO" : "Acceso DENEGADO"}
                >
                  <div className="mb-2">
                    <strong>{result.mensaje}</strong>
                  </div>
                  {!result.permitido && result.motivoRechazo && (
                    <div className="small">Motivo: {result.motivoRechazo}</div>
                  )}
                  {!result.permitido && result.tareaAlternativa && (
                    <div className="small">Tarea alternativa: {result.tareaAlternativa}</div>
                  )}
                </Alert>
              </div>
            </div>
          )}

          {esOperario && bloqueadoPorIntentos && (
            <div className="card mt-4">
              <div className="card-body">
                <Alert variant="warning" title="Opción alternativa asignada">
                  Superaste los {MAX_INTENTOS} intentos fallidos. Hoy no ingresarás a room_911; tu tarea alternativa es:
                  <div className="mt-2">
                    <strong>{tareaAlternativa || "Asignado a atención a clientes."}</strong>
                  </div>
                </Alert>
              </div>
            </div>
          )}

          {esOperario && perfil && !bloqueadoPorIntentos && (
            <div className="card mt-4">
              <div className="card-body">
                <h5 className="mb-3">
                  <Icon name="person-badge" className="me-2" />
                  Tu perfil de acceso
                </h5>
                <p className="mb-1">
                  Operario: <strong>{perfil.nombre}</strong>
                </p>
                <p className="mb-1">
                  Nivel ABAC:{" "}
                  <StatusPill tone="primary">Nivel {perfil.nivelAcceso}</StatusPill>{" "}
                  <span className="text-muted-2 small">{perfil.descripcionPerfil}</span>
                </p>
                {perfilLoading ? (
                  <Spinner />
                ) : (
                  <>
                    <h6 className="mt-3 mb-2">Medicamentos que puedes manipular</h6>
                    {perfil.categoriasPermitidas.length === 0 ? (
                      <p className="text-muted-2 small mb-2">Sin categorías asignadas.</p>
                    ) : (
                      <ul className="mb-3 small">
                        {perfil.categoriasPermitidas.map((c) => (
                          <li key={c.codigo}>
                            {c.nombre} <span className="text-muted-2">({c.codigo})</span>
                          </li>
                        ))}
                      </ul>
                    )}
                    <h6 className="mb-2">Programación de hoy en room_911</h6>
                    {perfil.cronogramaHoy.length === 0 ? (
                      <p className="text-muted-2 small mb-0">Hoy no hay categorías programadas.</p>
                    ) : (
                      <ul className="mb-0 small">
                        {perfil.cronogramaHoy.map((c) => (
                          <li key={c.codigo}>
                            {c.nombre} ({c.codigo}) —{" "}
                            <StatusPill tone={c.permitido ? "success" : "danger"}>
                              {c.permitido ? "Autorizado" : "No autorizado"}
                            </StatusPill>
                          </li>
                        ))}
                      </ul>
                    )}
                  </>
                )}
              </div>
            </div>
          )}
        </div>

        <div className="col-12 col-lg-7">
          <DataTable
            columns={columns}
            data={history}
            searchKeys={["idUsuario", "resultado", "mensaje"]}
            emptyText="Aún no hay evaluaciones registradas en esta sesión."
            toolbar={
              <span className="text-muted small">
                <Icon name="clock-history" className="me-1" />
                Historial de esta sesión
              </span>
            }
          />
        </div>
      </div>
    </>
  );
}
