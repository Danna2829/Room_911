import { Icon } from "../ui/Icon";

export function Topbar({ searchPlaceholder = "Buscar en el sistema...", onSearch }) {
  return (
    <header className="r911-topbar">
      <div className="topbar-search">
        <Icon name="search" />
        <input
          className="form-control"
          placeholder={searchPlaceholder}
          onChange={(e) => onSearch?.(e.target.value)}
        />
      </div>

      <div className="topbar-actions">
        <button className="topbar-icon-btn" aria-label="Notificaciones">
          <Icon name="bell" />
          <span className="badge-dot" />
        </button>
        <button className="topbar-icon-btn" aria-label="Ayuda">
          <Icon name="question-circle" />
        </button>
        <button className="topbar-icon-btn" aria-label="Perfil">
          <Icon name="person-circle" />
        </button>
      </div>
    </header>
  );
}
