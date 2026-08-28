import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { ToastProvider } from "./components/ui/Toast";
import Login from "./pages/Login";
import AppLayout from "./components/layout/AppLayout";
import Dashboard from "./pages/Dashboard";
import ComingSoon from "./pages/ComingSoon";

export default function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
        <Routes>
          <Route path="/login" element={<Login />} />

          <Route element={<AppLayout />}>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/garita" element={<ComingSoon title="Garita / Torniquete" />} />
            <Route path="/cronograma" element={<ComingSoon title="Cronograma Operativo" />} />
            <Route path="/monitor" element={<ComingSoon title="Monitor en Vivo" />} />
            <Route path="/usuarios" element={<ComingSoon title="Gestión de Usuarios" />} />
            <Route path="/inventario" element={<ComingSoon title="Inventario & Categorías" />} />
            <Route path="/reportes" element={<ComingSoon title="Reportes & Auditoría" />} />
            <Route path="/config" element={<ComingSoon title="Configuración" />} />
          </Route>

          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Routes>
      </ToastProvider>
    </BrowserRouter>
  );
}
