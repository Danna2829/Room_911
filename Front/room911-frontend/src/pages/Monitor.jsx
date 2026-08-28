import { useState } from "react";
import api from "../api/api";
import { PageHeader } from "../components/ui/PageHeader";
import { Icon } from "../components/ui/Icon";
import { Button } from "../components/ui/Button";
import { Alert } from "../components/ui/Alert";
import { StatusPill, Spinner } from "../components/ui/feedback";
import { TextField, SelectField } from "../components/ui/inputs";
import { Modal } from "../components/ui/Modal";
import { DataTable } from "../components/ui/DataTable";
import { useToast } from "../components/ui/Toast";
import { useFetch } from "../hooks/useFetch";
import { fmtDateTime } from "../utils/format";

export default function Monitor() {
  const toast = useToast();
  const { data: registros, loading, error } = useFetch(() => api.get("/acceso/monitor").then((r) => r.data));
  const { data: suspensiones, reload: reloadS } = useFetch(() => api.get("/guardia/suspensiones").then((r) => r.data));
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({ idUsuario: "", motivo: "SANCIÓN", fechaInicio: "", fechaFin: "" });

  const submit = async () => {
    try {
      await api.post("/guardia/suspender", {
        idUsuario: form.idUsuario,
        motivo: form.motivo,
        fechaInicio: form.fechaInicio || undefined,
        fechaFin: form.fechaFin || undefined,
      });
      toast.push({ type: "success", title: "Suspensión registrada" });
      setOpen(false);
      setForm({ idUsuario: "", motivo: "SANCIÓN", fechaInicio: "", fechaFin: "" });
      reloadS();
    } catch {
      toast.push({ type: "danger", title: "Error al suspender" });
    }
  };

  const desactivar = async (id) => {
    try {
      await api.put(`/guardia/suspensiones/${id}/desactivar`);
      toast.push({ type: "info", title: "Suspensión revocada" });
      reloadS();
    } catch {
      toast.push({ type: "danger", title: "Error al revocar" });
    }
  };

  const colAcc = [
    { key: "idUsuario", label: "Operario", sortable: true },
    { key: "timestamp", label: "Fecha/Hora", sortable: true, render: (r) => fmtDateTime(r.timestamp) },
    { key: "tipoEvento", label: "Evento", sortable: true },
    {
      key: "resultado",
      label: "Resultado",
      sortable: true,
      render: (r) => (
        <StatusPill tone={r.resultado === "PERMITIDO" ? "success" : r.resultado === "DENEGADO" ? "danger" : "info"}>
          {r.resultado}
        </StatusPill>
      ),
    },
    { key: "motivoRechazo", label: "Motivo de rechazo" },
  ];

  const colSus = [
    { key: "idUsuario", label: "Operario", sortable: true },
    { key: "motivo", label: "Motivo", sortable: true, render: (r) => <StatusPill tone="warning">{r.motivo}</StatusPill> },
    { key: "fechaInicio", label: "Inicio", sortable: true, render: (r) => fmtDateTime(r.fechaInicio) },
    { key: "fechaFin", label: "Fin", sortable: true, render: (r) => fmtDateTime(r.fechaFin) },
    {
      key: "activo",
      label: "Estado",
      sortable: true,
      render: (r) => <StatusPill tone={r.activo ? "success" : "neutral"}>{r.activo ? "Vigente" : "Revocada"}</StatusPill>,
    },
    {
      key: "acc",
      label: "",
      render: (r) =>
        r.activo ? (
          <Button variant="soft" size="sm" icon="slash-circle" onClick={() => desactivar(r.id)}>
            Revocar
          </Button>
        ) : (
          <span className="text-muted-2 small">—</span>
        ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Monitor en Vivo"
        subtitle="Auditoría de accesos en tiempo real y gestión de suspensiones"
        actions={<Button icon="person-x" onClick={() => setOpen(true)}>Suspender operario</Button>}
      />

      {loading ? (
        <div className="text-center py-5">
          <Spinner />
        </div>
      ) : error ? (
        <Alert variant="danger">No se pudo cargar el monitor. Verifica que el backend esté corriendo.</Alert>
      ) : (
        <DataTable
          columns={colAcc}
          data={registros}
          searchKeys={["idUsuario", "resultado", "tipoEvento", "motivoRechazo"]}
          filters={[
            {
              key: "resultado",
              label: "Todos los resultados",
              options: [
                { value: "PERMITIDO", label: "Permitido" },
                { value: "DENEGADO", label: "Denegado" },
              ],
            },
          ]}
          emptyText="Sin registros de auditoría."
        />
      )}

      <h5 className="mt-4 mb-3">
        <Icon name="slash-circle" className="me-2" />
        Suspensiones
      </h5>
      <DataTable columns={colSus} data={suspensiones} searchKeys={["idUsuario", "motivo"]} emptyText="No hay suspensiones registradas." />

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title="Suspender permiso de operario"
        footer={
          <>
            <Button variant="soft" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button icon="check-lg" onClick={submit}>
              Confirmar suspensión
            </Button>
          </>
        }
      >
        <TextField
          id="idu"
          label="ID de operario"
          icon="person-badge"
          placeholder="EMP-8821"
          value={form.idUsuario}
          onChange={(e) => setForm((f) => ({ ...f, idUsuario: e.target.value }))}
        />
        <SelectField
          id="mot"
          label="Motivo"
          value={form.motivo}
          onChange={(e) => setForm((f) => ({ ...f, motivo: e.target.value }))}
          options={[
            { value: "SANCIÓN", label: "SANCIÓN" },
            { value: "INCAPACIDAD", label: "INCAPACIDAD" },
            { value: "CAMBIO_TURNO", label: "CAMBIO_TURNO" },
          ]}
        />
        <TextField
          id="fi"
          label="Fecha/hora inicio"
          type="datetime-local"
          value={form.fechaInicio}
          onChange={(e) => setForm((f) => ({ ...f, fechaInicio: e.target.value }))}
        />
        <TextField
          id="ff"
          label="Fecha/hora fin (opcional)"
          type="datetime-local"
          value={form.fechaFin}
          onChange={(e) => setForm((f) => ({ ...f, fechaFin: e.target.value }))}
        />
      </Modal>
    </>
  );
}
