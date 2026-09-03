import { useState } from "react";
import api from "../api/api";
import { PageHeader } from "../components/ui/PageHeader";
import { Button } from "../components/ui/Button";
import { Alert } from "../components/ui/Alert";
import { StatusPill, Spinner } from "../components/ui/feedback";
import { TextField } from "../components/ui/inputs";
import { Modal } from "../components/ui/Modal";
import { DataTable } from "../components/ui/DataTable";
import { useToast } from "../components/ui/Toast";
import { useFetch } from "../hooks/useFetch";

export default function Medicamentos() {
  const toast = useToast();
  const { data: lista, loading, error, reload } = useFetch(() => api.get("/categorias").then((r) => r.data));
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState({ codigo: "", nombre: "", descripcion: "", esRestringido: false });

  const openNew = () => {
    setEditing(null);
    setForm({ codigo: "", nombre: "", descripcion: "", esRestringido: false });
    setOpen(true);
  };

  const openEdit = (c) => {
    setEditing(c);
    setForm({ codigo: c.codigo, nombre: c.nombre, descripcion: c.descripcion || "", esRestringido: c.esRestringido });
    setOpen(true);
  };

  const submit = async () => {
    if (!form.codigo.trim() || !form.nombre.trim()) {
      toast.push({ type: "danger", title: "Datos incompletos", message: "Código y nombre son obligatorios." });
      return;
    }
    try {
      if (editing) {
        await api.put(`/categorias/${editing.id}`, form);
        toast.push({ type: "success", title: "Medicamento actualizado" });
      } else {
        await api.post("/categorias", form);
        toast.push({ type: "success", title: "Medicamento creado" });
      }
      setOpen(false);
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al guardar" });
    }
  };

  const inhabilitar = async (id) => {
    try {
      await api.delete(`/categorias/${id}`);
      toast.push({ type: "info", title: "Medicamento inhabilitado" });
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al inhabilitar" });
    }
  };

  const reactivar = async (id) => {
    try {
      await api.put(`/categorias/${id}/reactivar`);
      toast.push({ type: "success", title: "Medicamento reactivado" });
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al reactivar" });
    }
  };

  const columns = [
    { key: "codigo", label: "Código", sortable: true },
    { key: "nombre", label: "Nombre", sortable: true },
    { key: "descripcion", label: "Descripción" },
    {
      key: "esRestringido",
      label: "Riesgo",
      sortable: true,
      render: (r) => (
        <StatusPill tone={r.esRestringido ? "danger" : "success"}>
          {r.esRestringido ? "Restringido" : "Estándar"}
        </StatusPill>
      ),
    },
    {
      key: "activo",
      label: "Estado",
      sortable: true,
      render: (r) => <StatusPill tone={r.activo ? "success" : "neutral"}>{r.activo ? "Activo" : "Inactivo"}</StatusPill>,
    },
    {
      key: "acciones",
      label: "",
      render: (r) => (
        <div className="d-flex gap-2">
          <Button variant="soft" size="sm" icon="pencil" onClick={() => openEdit(r)}>
            Editar
          </Button>
          {r.activo ? (
            <Button variant="outline" size="sm" icon="slash-circle" onClick={() => inhabilitar(r.id)}>
              Inhabilitar
            </Button>
          ) : (
            <Button variant="soft" size="sm" icon="arrow-counterclockwise" onClick={() => reactivar(r.id)}>
              Reactivar
            </Button>
          )}
        </div>
      ),
    },
  ];

  return (
    <>
      <PageHeader
        title="Gestión de Medicamentos"
        subtitle="Categorías por tipo de sustancia y matriz de riesgo ABAC"
        actions={<Button icon="capsule" onClick={openNew}>Nuevo medicamento</Button>}
      />

      {loading ? (
        <div className="text-center py-5">
          <Spinner />
        </div>
      ) : error ? (
        <Alert variant="danger">No se pudo cargar el catálogo. Verifica que el backend esté corriendo.</Alert>
      ) : (
        <DataTable
          columns={columns}
          data={lista}
          searchKeys={["codigo", "nombre", "descripcion"]}
          filters={[
            {
              key: "esRestringido",
              label: "Todos los riesgos",
              options: [
                { value: true, label: "Restringido" },
                { value: false, label: "Estándar" },
              ],
            },
          ]}
          emptyText="No hay medicamentos registrados."
        />
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title={editing ? "Editar medicamento" : "Nuevo medicamento"}
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
        <div className="row g-3">
          <div className="col-6">
            <TextField
              id="cod"
              label="Código (ej. TIPO_1)"
              value={form.codigo}
              onChange={(e) => setForm((f) => ({ ...f, codigo: e.target.value.toUpperCase() }))}
            />
          </div>
          <div className="col-6">
            <TextField
              id="nom"
              label="Nombre"
              value={form.nombre}
              onChange={(e) => setForm((f) => ({ ...f, nombre: e.target.value }))}
            />
          </div>
          <div className="col-12">
            <TextField
              id="desc"
              label="Descripción"
              value={form.descripcion}
              onChange={(e) => setForm((f) => ({ ...f, descripcion: e.target.value }))}
            />
          </div>
          <div className="col-12">
            <div className="form-check">
              <input
                className="form-check-input"
                type="checkbox"
                id="restringido"
                checked={form.esRestringido}
                onChange={(e) => setForm((f) => ({ ...f, esRestringido: e.target.checked }))}
              />
              <label className="form-check-label" htmlFor="restringido">
                Es medicamento restringido (alto riesgo, solo Nivel 3)
              </label>
            </div>
          </div>
        </div>
      </Modal>
    </>
  );
}
