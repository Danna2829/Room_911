import { useState } from "react";
import { PageHeader } from "../components/ui/PageHeader";
import { StatCard } from "../components/ui/feedback";
import { Button } from "../components/ui/Button";
import { Icon } from "../components/ui/Icon";
import { StatusPill } from "../components/ui/feedback";
import { DataTable } from "../components/ui/DataTable";
import { Modal } from "../components/ui/Modal";
import { Alert } from "../components/ui/Alert";
import { TextField, SelectField } from "../components/ui/inputs";

const ACCESOS = [
  { id: 1, operario: "Pedro Operario 3", nivel: "Nivel 3", medicamento: "Tipo 4 (Restringido)", estado: "Permitido", hora: "08:42" },
  { id: 2, operario: "Juan Operario 1", nivel: "Nivel 1", medicamento: "Tipo 1", estado: "Permitido", hora: "08:51" },
  { id: 3, operario: "Maria Operario 2", nivel: "Nivel 2", medicamento: "Tipo 5", estado: "En espera", hora: "09:03" },
  { id: 4, operario: "Carlos Guardia", nivel: "—", medicamento: "Supervisión", estado: "Permitido", hora: "09:10" },
  { id: 5, operario: "Juan Operario 1", nivel: "Nivel 1", medicamento: "Tipo 4 (Restringido)", estado: "Bloqueado", hora: "09:22" },
];

const ESTADO_TONE = { Permitido: "success", Bloqueado: "danger", "En espera": "warning" };

export default function Dashboard() {
  const [open, setOpen] = useState(false);

  const columns = [
    { key: "operario", label: "Operario", sortable: true },
    {
      key: "nivel",
      label: "Nivel ABAC",
      sortable: true,
      render: (r) => (r.nivel === "—" ? <span className="text-muted-2">—</span> : <StatusPill tone="info">{r.nivel}</StatusPill>),
    },
    { key: "medicamento", label: "Medicamento", sortable: true },
    {
      key: "estado",
      label: "Estado",
      sortable: true,
      render: (r) => <StatusPill tone={ESTADO_TONE[r.estado]}>{r.estado}</StatusPill>,
    },
    { key: "hora", label: "Hora", sortable: true },
  ];

  const filters = [
    {
      key: "estado",
      label: "Todos los estados",
      options: [
        { value: "Permitido", label: "Permitido" },
        { value: "Bloqueado", label: "Bloqueado" },
        { value: "En espera", label: "En espera" },
      ],
    },
  ];

  return (
    <>
      <PageHeader
        title="Panel General"
        subtitle="Visión operativa de la sala restringida room_911"
        actions={
          <>
            <Button variant="soft" icon="download">
              Exportar
            </Button>
            <Button icon="plus-lg" onClick={() => setOpen(true)}>
              Nuevo acceso
            </Button>
          </>
        }
      />

      <Alert variant="info" className="mb-4">
        Datos de demostración del design system. Las vistas de operación se construirán sobre estos mismos componentes.
      </Alert>

      <div className="row g-3 mb-4">
        <div className="col-12 col-sm-6 col-xl-3">
          <StatCard icon="shield-check" tone="primary" label="Accesos hoy" value="128" trend="+12% vs ayer" />
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <StatCard icon="x-octagon" tone="danger" label="Bloqueados" value="3" trend="Por matriz de riesgo" />
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <StatCard icon="capsule" tone="success" label="Medicamentos" value="42" trend="8 categorías" />
        </div>
        <div className="col-12 col-sm-6 col-xl-3">
          <StatCard icon="people" tone="warning" label="Operarios activos" value="17" trend="3 en sala" />
        </div>
      </div>

      <DataTable
        columns={columns}
        data={ACCESOS}
        searchKeys={["operario", "medicamento", "estado"]}
        filters={filters}
        emptyText="No hay accesos que coincidan con el filtro."
        toolbar={
          <Button variant="outline" size="sm" icon="funnel">
            Más filtros
          </Button>
        }
      />

      <Modal
        open={open}
        onClose={() => setOpen(false)}
        title="Registrar nuevo acceso"
        footer={
          <>
            <Button variant="soft" onClick={() => setOpen(false)}>
              Cancelar
            </Button>
            <Button icon="check-lg" onClick={() => setOpen(false)}>
              Confirmar
            </Button>
          </>
        }
      >
        <TextField id="op" label="ID de operario" icon="person-badge" placeholder="EMP-0000" />
        <SelectField
          id="med"
          label="Tipo de medicamento"
          options={[
            { value: "", label: "Selecciona..." },
            { value: "1", label: "Tipo 1" },
            { value: "2", label: "Tipo 2" },
            { value: "4", label: "Tipo 4 (Restringido)" },
            { value: "5", label: "Tipo 5" },
          ]}
        />
        <p className="text-muted small mb-0">
          <Icon name="info-circle" /> El motor ABAC evaluará riesgo y cronograma al guardar.
        </p>
      </Modal>
    </>
  );
}
