import { useState } from "react";
import api from "../api/api";
import { PageHeader } from "../components/ui/PageHeader";
import { Button } from "../components/ui/Button";
import { Icon } from "../components/ui/Icon";
import { Alert } from "../components/ui/Alert";
import { StatusPill } from "../components/ui/feedback";
import { TextField, SelectField } from "../components/ui/inputs";
import { DataTable } from "../components/ui/DataTable";
import { useToast } from "../components/ui/Toast";
import { fmtDateTime } from "../utils/format";

export default function Garita() {
  const toast = useToast();
  const [idUsuario, setIdUsuario] = useState("");
  const [tipoEvento, setTipoEvento] = useState("ENTRADA");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState(null);
  const [history, setHistory] = useState([]);

  const evaluar = async (e) => {
    e.preventDefault();
    if (!idUsuario.trim()) {
      toast.push({ type: "danger", title: "Datos incompletos", message: "Ingresa el ID de usuario." });
      return;
    }
    setLoading(true);
    try {
      const { data } = await api.post("/acceso/evaluar", { idUsuario: idUsuario.trim(), tipoEvento });
      setResult(data);
      setHistory((h) => [data, ...h].slice(0, 10));
      toast.push({
        type: data.permitido ? "success" : "danger",
        title: data.permitido ? "Acceso permitido" : "Acceso denegado",
        message: data.mensaje,
      });
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

  return (
    <>
      <PageHeader
        title="Garita / Torniquete"
        subtitle="Simulador táctil de evaluación de acceso (motor ABAC)"
      />
      <div className="row g-4">
        <div className="col-12 col-lg-5">
          <div className="card">
            <div className="card-body">
              <h5 className="mb-3">
                <Icon name="door-closed" className="me-2" />
                Evaluar acceso
              </h5>
              <form onSubmit={evaluar}>
                <TextField
                  id="idU"
                  label="ID de usuario"
                  icon="person-badge"
                  placeholder="EMP-8821"
                  value={idUsuario}
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
                  Evaluar acceso
                </Button>
              </form>
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
