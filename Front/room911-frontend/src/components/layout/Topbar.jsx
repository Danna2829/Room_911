import { useNavigate } from "react-router-dom";
import { Icon } from "../ui/Icon";
import { useAuth } from "../../auth/AuthContext";

export function Topbar({ searchPlaceholder = "Buscar en el sistema...", onSearch }) {
  const navigate = useNavigate();
  const { logout } = useAuth();

  const onLogout = () => {
    logout();
    navigate("/login");
  };

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
        <button className="btn btn-soft btn-sm" onClick={onLogout}>
          <Icon name="box-arrow-right" /> Salir
        </button>
      </div>
    </header>
  );
}
