import { Outlet } from "react-router-dom";
import { Sidebar } from "./Sidebar";
import { Topbar } from "./Topbar";
import { useAuth } from "../../auth/AuthContext";

const NAV = [
  {
    label: "Operación",
    items: [
      { to: "/dashboard", label: "Panel General", icon: "speedometer2" },
      { to: "/garita", label: "Garita / Torniquete", icon: "door-closed" },
      { to: "/cronograma", label: "Cronograma", icon: "calendar3" },
      { to: "/monitor", label: "Monitor en Vivo", icon: "activity" },
    ],
  },
  {
    label: "Gestión",
    items: [
      { to: "/usuarios", label: "Usuarios", icon: "people" },
      { to: "/inventario", label: "Inventario", icon: "capsule" },
      { to: "/reportes", label: "Reportes & Auditoría", icon: "clipboard-data" },
    ],
  },
  {
    label: "Sistema",
    items: [{ to: "/config", label: "Configuración", icon: "gear" }],
  },
];

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
      <Sidebar sections={NAV} user={buildUser(user)} />
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
