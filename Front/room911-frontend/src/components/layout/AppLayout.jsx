import { Outlet } from "react-router-dom";
import { Sidebar } from "./Sidebar";
import { Topbar } from "./Topbar";
import { useAuth } from "../../auth/AuthContext";
import { sectionsFor } from "../../auth/roles";

function buildUser(user) {
  if (!user) return null;
  const initials = (user.nombre || "U")
    .split(" ")
    .map((s) => s[0])
    .join("")
    .slice(0, 2)
    .toUpperCase();
  return { name: user.nombre, role: user.rol, initials };
}

export default function AppLayout() {
  const { user } = useAuth();
  return (
    <div className="app-shell">
      <Sidebar sections={sectionsFor(user?.rol)} user={buildUser(user)} />
      <div className="r911-main">
        <Topbar />
        <main className="r911-content">
          <div className="r911-content-inner">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
}
