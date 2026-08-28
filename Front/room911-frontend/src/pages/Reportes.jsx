import api from "../api/api";
import { PageHeader } from "../components/ui/PageHeader";
import { Button } from "../components/ui/Button";
import { Alert } from "../components/ui/Alert";
import { StatusPill, Spinner, StatCard } from "../components/ui/feedback";
import { DataTable } from "../components/ui/DataTable";
import { useToast } from "../components/ui/Toast";
import { useFetch } from "../hooks/useFetch";
import { fmtDateTime } from "../utils/format";

export default function Reportes() {
  const toast = useToast();
  const { data: accesos, loading, error } = useFetch(() => api.get("/reportes/accesos").then((r) => r.data));

  const exportCSV = async () => {
    try {
      const { data } = await api.get("/reportes/exportar/csv", { responseType: "blob" });
      const url = URL.createObjectURL(data);
      const a = document.createElement("a");
      a.href = url;
      a.download = "reporte_room911.csv";
      a.click();
      URL.revokeObjectURL(url);
      toast.push({ type: "success", title: "Reporte exportado" });
    } catch {
      toast.push({ type: "danger", title: "Error al exportar" });
    }
  };

  const permitidos = accesos.filter((a) => a.resultado === "PERMITIDO").length;
  const denegados = accesos.length - permitidos;

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
    { key: "motivoRechazo", label: "Motivo de rechazo" },
  ];

  return (
    <>
      <PageHeader
        title="Reportes & Auditoría"
        subtitle="Trazabilidad de accesos y exportación de evidencias"
        actions={<Button icon="download" onClick={exportCSV}>Exportar CSV</Button>}
      />

      <div className="row g-3 mb-4">
        <div className="col-12 col-sm-4">
          <StatCard icon="list-check" tone="primary" label="Total de accesos" value={accesos.length} />
        </div>
        <div className="col-12 col-sm-4">
          <StatCard icon="check-circle" tone="success" label="Permitidos" value={permitidos} />
        </div>
        <div className="col-12 col-sm-4">
          <StatCard icon="x-circle" tone="danger" label="Denegados" value={denegados} />
        </div>
      </div>

      {loading ? (
        <div className="text-center py-5">
          <Spinner />
        </div>
      ) : error ? (
        <Alert variant="danger">No se pudo cargar el reporte. Verifica que el backend esté corriendo.</Alert>
      ) : (
        <DataTable
          columns={columns}
          data={accesos}
          searchKeys={["idUsuario", "resultado", "tipoEvento"]}
          emptyText="Sin registros de auditoría."
        />
      )}
    </>
  );
}
