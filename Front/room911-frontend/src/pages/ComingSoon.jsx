import { PageHeader } from "../components/ui/PageHeader";
import { EmptyState } from "../components/ui/feedback";
import { Button } from "../components/ui/Button";

export default function ComingSoon({ title, subtitle }) {
  return (
    <>
      <PageHeader title={title} subtitle={subtitle || "Módulo en construcción"} />
      <div className="card">
        <EmptyState
          icon="hammer"
          title="Vista en desarrollo"
          message="Esta sección se construirá sobre el mismo design system (tablas CRUD, buscadores, filtros, modales y feedback estandarizados)."
          action={
            <Button variant="soft" icon="arrow-left" onClick={() => window.history.back()}>
              Volver
            </Button>
          }
        />
      </div>
    </>
  );
}
