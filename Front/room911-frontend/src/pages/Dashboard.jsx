import { useNavigate } from "react-router-dom";
import api from "../api/api";
import { PageHeader } from "../components/ui/PageHeader";
import { StatCard, StatusPill, Spinner } from "../components/ui/feedback";
import { Button } from "../components/ui/Button";
import { Alert } from "../components/ui/Alert";
import { DataTable } from "../components/ui/DataTable";
import { useFetch } from "../hooks/useFetch";
import { fmtDateTime } from "../utils/format";

function apiGetResumen() {
  return api.get("/dashboard/resumen").then((r) => r.data);
}

export default function Dashboard() {
  const navigate = useNavigate();
  const { data: resumen, loading, error } = useFetch(() => apiGetResumen());

  const columns = [
    { key: "idUsuario", label: "Operario", sortable: true },
    { key: "timestamp", label: "Fecha/Hora", sortable: true, render: (r) => fmtDateTime(r.timestamp) },
    { key: "tipoEvento", label: "Evento", sortable: true },
    {
      key: "resultado",
      label: "Resultado",
      sortable: true,
      render: (r) => (
        <StatusPill tone={r.resultado === "PERMITIDO" ? "success" : "danger"}>{r.resultado}</StatusPill>
      ),
    },
    { key: "motivoRechazo", label: "Motivo de rechazo", render: (r) => r.motivoRechazo || "—" },
  ];

  return (
    <>
      <PageHeader
        title="Panel General"
        subtitle="Visión operativa de la sala restringida room_911"
        actions={
          <Button icon="activity" onClick={() => navigate("/monitor")}>
            Ver monitor en vivo
          </Button>
        }
      />

      {loading ? (
        <div className="text-center py-5">
          <Spinner />
        </div>
      ) : error ? (
        <Alert variant="danger">No se pudo cargar el resumen. Verifica que el backend esté corriendo.</Alert>
      ) : (
        <>
          <div className="row g-3 mb-4">
            <div className="col-12 col-sm-6 col-xl-3">
              <StatCard icon="shield-check" tone="primary" label="Accesos hoy" value={resumen.accesosHoy} />
            </div>
            <div className="col-12 col-sm-6 col-xl-3">
              <StatCard icon="x-octagon" tone="danger" label="Denegados hoy" value={resumen.denegadosHoy} />
            </div>
            <div className="col-12 col-sm-6 col-xl-3">
              <StatCard icon="capsule" tone="success" label="Medicamentos activos" value={resumen.medicamentosActivos} />
            </div>
            <div className="col-12 col-sm-6 col-xl-3">
              <StatCard icon="people" tone="warning" label="Operarios activos" value={resumen.operariosActivos} />
            </div>
          </div>

          <div className="row g-3 mb-4">
            <div className="col-12 col-sm-6">
              <Alert variant="info">
                <strong>Programación de hoy:</strong>{" "}
                {resumen.programadosHoy > 0
                  ? `${resumen.programadosHoy} categoría(s) activa(s) en room_911`
                  : "Sin categorías programadas por Secretaría"}
              </Alert>
            </div>
            <div className="col-12 col-sm-6">
              <Alert variant={resumen.suspensionesVigentes > 0 ? "warning" : "success"}>
                <strong>Suspensiones vigentes:</strong> {resumen.suspensionesVigentes} (a cargo de la Guardia)
              </Alert>
            </div>
          </div>

          <h5 className="mb-3">Últimos accesos registrados</h5>
          <DataTable
            columns={columns}
            data={resumen.ultimosAccesos || []}
            searchKeys={["idUsuario", "resultado", "tipoEvento"]}
            emptyText="Aún no hay accesos registrados."
          />
        </>
      )}
    </>
  );
}
