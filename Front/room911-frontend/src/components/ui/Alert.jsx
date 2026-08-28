import { Icon } from "./Icon";

export function Alert({ variant = "info", icon, title, children, onClose }) {
  const ic = icon || (variant === "success" ? "check-circle-fill" : variant === "danger" ? "x-circle-fill" : variant === "warning" ? "exclamation-triangle-fill" : "info-circle-fill");
  return (
    <div className={`alert-r911 alert-${variant}`} role="alert">
      <Icon name={ic} />
      <div style={{ flex: 1 }}>
        {title && <div style={{ fontWeight: 700, marginBottom: 2 }}>{title}</div>}
        {children}
      </div>
      {onClose && (
        <button className="btn-close ms-2" onClick={onClose} aria-label="Cerrar" />
      )}
    </div>
  );
}
