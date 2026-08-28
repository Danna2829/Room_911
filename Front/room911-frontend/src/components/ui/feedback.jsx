import { Icon } from "./Icon";

export function StatusPill({ tone = "neutral", dot = true, children }) {
  return (
    <span className={`badge-soft badge-${tone}`}>
      {dot && <span className="dot" />}
      {children}
    </span>
  );
}

export function Spinner({ size }) {
  return (
    <span
      className={`spinner-border text-primary ${size === "sm" ? "spinner-border-sm" : ""}`}
      role="status"
      aria-hidden="true"
    />
  );
}

export function EmptyState({ icon = "inbox", title, message, action }) {
  return (
    <div className="empty-state">
      <div className="empty-icon">
        <Icon name={icon} />
      </div>
      <h5 className="mb-1">{title}</h5>
      {message && <p className="mb-3">{message}</p>}
      {action}
    </div>
  );
}

export function StatCard({ icon = "graph-up", label, value, trend, tone = "primary" }) {
  const bg = tone === "primary" ? "var(--brand-50)" : tone === "success" ? "var(--success-bg)" : tone === "warning" ? "var(--warning-bg)" : "var(--info-bg)";
  const fg = tone === "primary" ? "var(--brand-600)" : tone === "success" ? "var(--success)" : tone === "warning" ? "var(--warning)" : "var(--info)";
  return (
    <div className="card-stat">
      <div className="d-flex justify-content-between align-items-start">
        <span className="stat-label">{label}</span>
        <span className="stat-icon" style={{ background: bg, color: fg }}>
          <Icon name={icon} />
        </span>
      </div>
      <div className="stat-value mt-2">{value}</div>
      {trend && <div className="small mt-1 text-muted">{trend}</div>}
    </div>
  );
}
