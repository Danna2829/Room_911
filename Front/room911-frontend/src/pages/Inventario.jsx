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
import { fmtDateTime } from "../utils/format";

export default function Inventario() {
  const toast = useToast();
  const { data: items, loading, error, reload } = useFetch(() => api.get("/inventario").then((r) => r.data));
  const { data: cats } = useFetch(() => api.get("/categorias").then((r) => r.data));
  const [open, setOpen] = useState(false);
  const [tipo, setTipo] = useState("ENTRADA");
  const [form, setForm] = useState({ idCategoria: "", cantidad: "", lote: "", observaciones: "" });

  const catName = (id) => cats.find((c) => String(c.id) === String(id))?.nombre || "—";

  const submit = async () => {
    try {
      const payload = {
        idCategoria: form.idCategoria ? Number(form.idCategoria) : null,
        cantidad: form.cantidad ? Number(form.cantidad) : null,
        lote: form.lote,
        observaciones: form.observaciones,
      };
      await api.post(`/inventario/${tipo === "ENTRADA" ? "entrada" : "salida"}`, payload);
      toast.push({ type: "success", title: `${tipo} registrada` });
      setOpen(false);
      setForm({ idCategoria: "", cantidad: "", lote: "", observaciones: "" });
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al registrar movimiento" });
    }
  };

  const inhabilitar = async (id) => {
    try {
      await api.delete(`/inventario/${id}`);
      toast.push({ type: "info", title: "Movimiento anulado" });
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al anular" });
    }
  };

  const reactivar = async (id) => {
    try {
      await api.put(`/inventario/${id}/reactivar`);
      toast.push({ type: "success", title: "Movimiento reactivado" });
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al reactivar" });
    }
  };

  const columns = [
    { key: "id", label: "#", sortable: true },
    { key: "categoria", label: "Categoría", sortable: true, render: (r) => catName(r.idCategoria) },
    { key: "cantidad", label: "Cantidad", sortable: true },
    {
      key: "tipoMovimiento",
      label: "Tipo",
      sortable: true,
      render: (r) => <StatusPill tone={r.tipoMovimiento === "ENTRADA" ? "success" : "info"}>{r.tipoMovimiento}</StatusPill>,
    },
    { key: "lote", label: "Lote" },
    { key: "timestamp", label: "Fecha/Hora", sortable: true, render: (r) => fmtDateTime(r.timestamp) },
    {
      key: "activo",
      label: "Estado",
      sortable: true,
      render: (r) => <StatusPill tone={r.activo ? "success" : "neutral"}>{r.activo ? "Activo" : "Anulado"}</StatusPill>,
    },
    {
      key: "acc",
      label: "",
      render: (r) =>
        r.activo ? (
          <Button variant="soft" size="sm" icon="slash-circle" onClick={() => inhabilitar(r.id)}>
            Anular
          </Button>
        ) : (
          <Button variant="soft" size="sm" icon="arrow-counterclockwise" onClick={() => reactivar(r.id)}>
            Reactivar
          </Button>
        ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Inventario & Categorías"
        subtitle="Movimientos de medicamentos en room_911"
        actions={<Button icon="box-arrow-in-down" onClick={() => setOpen(true)}>Registrar movimiento</Button>}
      />

      {loading ? (
        <div className="text-center py-5">
          <Spinner />
        </div>
      ) : error ? (
        <Alert variant="danger">No se pudo cargar el inventario. Verifica que el backend esté corriendo.</Alert>
      ) : (
        <DataTable
          columns={columns}
          data={items}
          searchKeys={["lote", "tipoMovimiento"]}
          filters={[
            {
              key: "tipoMovimiento",
              label: "Todos los tipos",
              options: [
                { value: "ENTRADA", label: "Entradas" },
                { value: "SALIDA", label: "Salidas" },
              ],
            },
          ]}
          emptyText="No hay movimientos de inventario."
        />
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title="Registrar movimiento de inventario"
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
        <SelectField
          id="tipo"
          label="Tipo de movimiento"
          value={tipo}
          onChange={(e) => setTipo(e.target.value)}
          options={[
            { value: "ENTRADA", label: "ENTRADA" },
            { value: "SALIDA", label: "SALIDA" },
          ]}
        />
        <SelectField
          id="cat"
          label="Categoría"
          value={form.idCategoria}
          onChange={(e) => setForm((f) => ({ ...f, idCategoria: e.target.value }))}
          options={[{ value: "", label: "Selecciona..." }, ...cats.map((c) => ({ value: String(c.id), label: c.nombre }))]}
        />
        <TextField
          id="cant"
          label="Cantidad"
          type="number"
          icon="123"
          value={form.cantidad}
          onChange={(e) => setForm((f) => ({ ...f, cantidad: e.target.value }))}
        />
        <TextField
          id="lote"
          label="Lote"
          icon="upc"
          value={form.lote}
          onChange={(e) => setForm((f) => ({ ...f, lote: e.target.value }))}
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
