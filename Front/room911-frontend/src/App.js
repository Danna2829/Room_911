import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ToastProvider } from "./components/ui/Toast";
import { AuthProvider } from "./auth/AuthContext";
import RequireAuth from "./auth/RequireAuth";
import { canAccess, homeFor } from "./auth/roles";
import { useAuth } from "./auth/AuthContext";
import Login from "./pages/Login";
import AppLayout from "./components/layout/AppLayout";
import Dashboard from "./pages/Dashboard";
import Garita from "./pages/Garita";
import Cronograma from "./pages/Cronograma";
import Monitor from "./pages/Monitor";
import Usuarios from "./pages/Usuarios";
import Medicamentos from "./pages/Medicamentos";
import Inventario from "./pages/Inventario";
import Reportes from "./pages/Reportes";
import ComingSoon from "./pages/ComingSoon";

// Protege cada ruta segun el rol del usuario autenticado.
function RoleRoute({ path, children }) {
  const { user } = useAuth();
  if (!canAccess(user?.rol, path)) return <Navigate to={homeFor(user?.rol)} replace />;
  return children;
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ToastProvider>
          <Routes>
            <Route path="/login" element={<Login />} />

            <Route
              element={
                <RequireAuth>
                  <AppLayout />
                </RequireAuth>
              }
            >
              <Route path="/" element={<HomeRedirect />} />
              <Route path="/dashboard" element={<RoleRoute path="/dashboard"><Dashboard /></RoleRoute>} />
              <Route path="/garita" element={<RoleRoute path="/garita"><Garita /></RoleRoute>} />
              <Route path="/cronograma" element={<RoleRoute path="/cronograma"><Cronograma /></RoleRoute>} />
              <Route path="/monitor" element={<RoleRoute path="/monitor"><Monitor /></RoleRoute>} />
              <Route path="/usuarios" element={<RoleRoute path="/usuarios"><Usuarios /></RoleRoute>} />
              <Route path="/medicamentos" element={<RoleRoute path="/medicamentos"><Medicamentos /></RoleRoute>} />
              <Route path="/inventario" element={<RoleRoute path="/inventario"><Inventario /></RoleRoute>} />
              <Route path="/reportes" element={<RoleRoute path="/reportes"><Reportes /></RoleRoute>} />
              <Route path="/config" element={<RoleRoute path="/config"><ComingSoon title="Configuración" /></RoleRoute>} />
            </Route>

            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </ToastProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

// Lleva a cada usuario a la pantalla inicial de su rol.
function HomeRedirect() {
  const { user } = useAuth();
  return <Navigate to={homeFor(user?.rol)} replace />;
}
