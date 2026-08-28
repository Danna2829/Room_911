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
import { useAuth } from "../auth/AuthContext";

const ROLES = [
  { value: "SUPERADMINISTRADOR", label: "SUPERADMINISTRADOR" },
  { value: "ADMINISTRADOR", label: "ADMINISTRADOR" },
  { value: "GUARDIA_SEGURIDAD", label: "GUARDIA_SEGURIDAD" },
  { value: "OPERARIO", label: "OPERARIO" },
  { value: "SECRETARIA", label: "SECRETARIA" },
];

export default function Usuarios() {
  const toast = useToast();
  const { user } = useAuth();
  const { data: lista, loading, error, reload } = useFetch(() => api.get("/admin/listar-usuarios").then((r) => r.data));
  const [open, setOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [reset, setReset] = useState({ open: false, target: null, loading: false, tempPassword: "", error: "" });
  const [form, setForm] = useState({
    idUsuario: "",
    nombre: "",
    apellido: "",
    correo: "",
    rol: "OPERARIO",
    contrasena: "",
    estado: true,
  });

  const openNew = () => {
    setEditing(null);
    setForm({ idUsuario: "", nombre: "", apellido: "", correo: "", rol: "OPERARIO", contrasena: "", estado: true });
    setOpen(true);
  };
  const openEdit = (u) => {
    setEditing(u);
    setForm({ ...u, contrasena: "" });
    setOpen(true);
  };

  const submit = async () => {
    try {
      const payload = { ...form };
      if (!payload.contrasena) delete payload.contrasena;
      if (editing) {
        await api.put(`/admin/editar-usuario/${editing.idUsuario}`, payload);
        toast.push({ type: "success", title: "Usuario actualizado" });
      } else {
        await api.post("/admin/crear-usuario", payload);
        toast.push({ type: "success", title: "Usuario creado" });
      }
      setOpen(false);
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al guardar usuario" });
    }
  };

  const eliminar = async (id) => {
    try {
      await api.delete(`/admin/eliminar-usuario/${id}`);
      toast.push({ type: "info", title: "Usuario eliminado" });
      reload();
    } catch {
      toast.push({ type: "danger", title: "Error al eliminar" });
    }
  };

  const resetear = async (u) => {
    setReset({ open: true, target: u, loading: true, tempPassword: "", error: "" });
    try {
      const { data } = await api.post("/admin/reset-password", {
        idUsuario: u.idUsuario,
        solicitanteId: user?.idUsuario,
      });
      setReset({ open: true, target: u, loading: false, tempPassword: data.tempPassword, error: "" });
      toast.push({ type: "success", title: "Contraseña restablecida" });
      reload();
    } catch (err) {
      const m = err.response?.data?.mensaje || "No se pudo restablecer la contraseña.";
      setReset({ open: true, target: u, loading: false, tempPassword: "", error: m });
    }
  };

  const columns = [
    { key: "idUsuario", label: "ID", sortable: true },
    { key: "nombre", label: "Nombre", sortable: true, render: (r) => `${r.nombre || ""} ${r.apellido || ""}`.trim() },
    { key: "correo", label: "Correo", sortable: true },
    {
      key: "rol",
      label: "Rol",
      sortable: true,
      render: (r) => {
        const tone =
          r.rol === "SUPERADMINISTRADOR"
            ? "danger"
            : r.rol === "ADMINISTRADOR"
            ? "info"
            : r.rol === "GUARDIA_SEGURIDAD"
            ? "warning"
            : "success";
        return <StatusPill tone={tone}>{r.rol}</StatusPill>;
      },
    },
    {
      key: "estado",
      label: "Estado",
      sortable: true,
      render: (r) => <StatusPill tone={r.estado ? "success" : "neutral"}>{r.estado ? "Activo" : "Inactivo"}</StatusPill>,
    },
      {
        key: "acc",
        label: "",
        render: (r) => (
          <div className="d-flex gap-2">
            {user?.rol === "SUPERADMINISTRADOR" && (
              <Button variant="soft" size="sm" icon="key" onClick={() => resetear(r)}>
                Restablecer
              </Button>
            )}
            <Button variant="soft" size="sm" icon="pencil" onClick={() => openEdit(r)}>
              Editar
            </Button>
            <Button variant="outline" size="sm" icon="trash" onClick={() => eliminar(r.idUsuario)}>
              Eliminar
            </Button>
          </div>
        ),
      },
  ];

  return (
    <>
      <PageHeader
        title="Gestión de Usuarios"
        subtitle="Administración de cuentas, roles y niveles de acceso"
        actions={<Button icon="person-plus" onClick={openNew}>Nuevo usuario</Button>}
      />

      {loading ? (
        <div className="text-center py-5">
          <Spinner />
        </div>
      ) : error ? (
        <Alert variant="danger">No se pudo cargar los usuarios. Verifica que el backend esté corriendo.</Alert>
      ) : (
        <DataTable
          columns={columns}
          data={lista}
          searchKeys={["idUsuario", "nombre", "correo", "rol"]}
          filters={[
            {
              key: "rol",
              label: "Todos los roles",
              options: ROLES,
            },
          ]}
          emptyText="No hay usuarios registrados."
        />
      )}

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title={editing ? "Editar usuario" : "Nuevo usuario"}
        footer={
          <>
            <Button variant="soft" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button icon="check-lg" onClick={submit}>
              {editing ? "Guardar cambios" : "Crear usuario"}
            </Button>
          </>
        }
      >
        <div className="row g-3">
          <div className="col-6">
            <TextField
              id="idu"
              label="ID de usuario"
              icon="hash"
              placeholder="EMP-0001"
              value={form.idUsuario}
              disabled={!!editing}
              onChange={(e) => setForm((f) => ({ ...f, idUsuario: e.target.value }))}
            />
          </div>
          <div className="col-6">
            <SelectField
              id="rol"
              label="Rol"
              value={form.rol}
              onChange={(e) => setForm((f) => ({ ...f, rol: e.target.value }))}
              options={ROLES}
            />
          </div>
          <div className="col-6">
            <TextField id="nom" label="Nombre" value={form.nombre} onChange={(e) => setForm((f) => ({ ...f, nombre: e.target.value }))} />
          </div>
          <div className="col-6">
            <TextField id="ape" label="Apellido" value={form.apellido} onChange={(e) => setForm((f) => ({ ...f, apellido: e.target.value }))} />
          </div>
          <div className="col-12">
            <TextField
              id="cor"
              label="Correo"
              type="email"
              icon="envelope"
              value={form.correo}
              onChange={(e) => setForm((f) => ({ ...f, correo: e.target.value }))}
            />
          </div>
          <div className="col-12">
            <TextField
              id="con"
              label={editing ? "Contraseña (dejar vacío para no cambiar)" : "Contraseña"}
              type="password"
              icon="lock"
              value={form.contrasena}
              onChange={(e) => setForm((f) => ({ ...f, contrasena: e.target.value }))}
            />
          </div>
        </div>
      </Modal>

      <Modal
        open={reset.open}
        onClose={() => setReset({ open: false, target: null, loading: false, tempPassword: "", error: "" })}
        title="Restablecer contraseña"
        footer={
          <Button onClick={() => setReset({ open: false, target: null, loading: false, tempPassword: "", error: "" })}>
            Cerrar
          </Button>
        }
      >
        {reset.loading ? (
          <div className="text-center py-3">
            <Spinner />
          </div>
        ) : reset.error ? (
          <Alert variant="danger">{reset.error}</Alert>
        ) : reset.tempPassword ? (
          <>
            <p>
              Contraseña temporal generada para <strong>{reset.target?.correo}</strong>:
            </p>
            <div className="alert alert-light border rounded p-3 mb-2">
              <code style={{ fontSize: "1.1rem" }}>{reset.tempPassword}</code>
            </div>
            <Alert variant="info">
              Entrégala al usuario por canal seguro. Deberá cambiarla luego desde la edición de su usuario.
            </Alert>
          </>
        ) : null}
      </Modal>
    </>
  );
}
