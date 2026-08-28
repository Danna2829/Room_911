import { useState } from "react";
import api from "../api/api";
import { PageHeader } from "../components/ui/PageHeader";
import { Button } from "../components/ui/Button";
import { Alert } from "../components/ui/Alert";
import { StatusPill, Spinner } from "../components/ui/feedback";
import { TextField, SelectField } from "../components/ui/inputs";
import { Modal } from "../components/ui/Modal";
import { DataTable } from "../components/ui/DataTable";
import { useToast } from "../components/ui/Toast";
import { useFetch } from "../hooks/useFetch";
import { fmtDate } from "../utils/format";

export default function Cronograma() {
  const toast = useToast();
  const { data: lista, loading, error, reload } = useFetch(() => api.get("/cronograma").then((r) => r.data));
  const { data: cats } = useFetch(() => api.get("/categorias").then((r) => r.data));
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({ fecha: "", idCategoria: "", observaciones: "" });

  const catName = (id) => cats.find((c) => String(c.id) === String(id))?.nombre || "—";

  const submit = async () => {
    try {
      await api.post("/cronograma", {
        fecha: form.fecha || undefined,
        idCategoria: form.idCategoria ? Number(form.idCategoria) : null,
        observaciones: form.observaciones,
      });
      toast.push({ type: "success", title: "Cronograma guardado" });
      setOpen(false);
      setForm({ fecha: "", idCategoria: "", observaciones: "" });
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error", message: "No se pudo guardar." });
    }
  };

  const inhabilitar = async (id) => {
    try {
      await api.delete(`/cronograma/${id}`);
      toast.push({ type: "info", title: "Programación inhabilitada" });
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al inhabilitar" });
    }
  };

  const columns = [
    { key: "fecha", label: "Fecha", sortable: true, render: (r) => fmtDate(r.fecha) },
    { key: "categoria", label: "Categoría", sortable: true, render: (r) => catName(r.idCategoria) },
    { key: "observaciones", label: "Observaciones" },
    {
      key: "activo",
      label: "Estado",
      sortable: true,
      render: (r) => <StatusPill tone={r.activo ? "success" : "neutral"}>{r.activo ? "Activo" : "Inactivo"}</StatusPill>,
    },
    {
      key: "acciones",
      label: "",
      render: (r) =>
        r.activo ? (
          <Button variant="soft" size="sm" icon="slash-circle" onClick={() => inhabilitar(r.id)}>
            Inhabilitar
          </Button>
        ) : (
          <span className="text-muted-2 small">—</span>
        ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Cronograma Operativo"
        subtitle="Programación diaria de la categoría de medicamento en room_911"
        actions={<Button icon="calendar-plus" onClick={() => setOpen(true)}>Programar día</Button>}
      />

      {loading ? (
        <div className="text-center py-5">
          <Spinner />
        </div>
      ) : error ? (
        <Alert variant="danger">No se pudo cargar el cronograma. Verifica que el backend esté corriendo.</Alert>
      ) : (
        <DataTable
          columns={columns}
          data={lista}
          searchKeys={["observaciones"]}
          filters={[
            {
              key: "activo",
              label: "Todos los estados",
              options: [
                { value: "true", label: "Activos" },
                { value: "false", label: "Inactivos" },
              ],
            },
          ]}
          emptyText="No hay programaciones registradas."
        />
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title="Programar cronograma diario"
        footer={
          <>
            <Button variant="soft" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button icon="check-lg" onClick={submit}>
              Guardar
            </Button>
          </>
        }
      >
        <TextField
          id="fec"
          label="Fecha"
          type="date"
          value={form.fecha}
          onChange={(e) => setForm((f) => ({ ...f, fecha: e.target.value }))}
        />
        <SelectField
          id="cat"
          label="Categoría de medicamento"
          value={form.idCategoria}
          onChange={(e) => setForm((f) => ({ ...f, idCategoria: e.target.value }))}
          options={[{ value: "", label: "Selecciona..." }, ...cats.map((c) => ({ value: String(c.id), label: c.nombre }))]}
        />
        <TextField
          id="obs"
          label="Observaciones"
          icon="card-text"
          value={form.observaciones}
          onChange={(e) => setForm((f) => ({ ...f, observaciones: e.target.value }))}
        />
      </Modal>
    </>
  );
}
