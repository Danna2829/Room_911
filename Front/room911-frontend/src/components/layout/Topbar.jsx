import { useNavigate } from "react-router-dom";
import { Icon } from "../ui/Icon";
import { useAuth } from "../../auth/AuthContext";

export function Topbar() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  const onLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <header className="r911-topbar">
      <div className="topbar-actions">
        <button className="btn btn-soft btn-sm" onClick={onLogout}>
          <Icon name="box-arrow-right" /> Salir
        </button>
      </div>
    </header>
  );
}
