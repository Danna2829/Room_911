import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Icon } from "../components/ui/Icon";
import { TextField } from "../components/ui/inputs";
import { Button } from "../components/ui/Button";
import { useToast } from "../components/ui/Toast";
import { useAuth } from "../auth/AuthContext";

const FEATURES = [
  {
    icon: "shield-check",
    title: "Control de acceso ABAC/RBAC",
    text: "Matriz de riesgo y cronograma evaluada en tiempo real en cada evento.",
  },
  {
    icon: "clock-history",
    title: "Trazabilidad total",
    text: "Registro de auditoría inalterable de cada ingreso a la sala restringida.",
  },
  {
    icon: "graph-up-arrow",
    title: "Visión operativa",
    text: "Monitoreo en vivo y reportes exportables para cumplimiento farmacéutico.",
  },
];

export default function Login() {
  const navigate = useNavigate();
  const toast = useToast();
  const { login } = useAuth();
  const [form, setForm] = useState({ email: "", password: "" });
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);

  const update = (k) => (e) => setForm((f) => ({ ...f, [k]: e.target.value }));

  const validate = () => {
    const e = {};
    if (!form.email) e.email = "El correo es obligatorio";
    else if (!/^[^@\s]+@[^@\s]+\.[^@\s]+$/.test(form.email)) e.email = "Correo no válido";
    if (!form.password) e.password = "La contraseña es obligatoria";
    setErrors(e);
    return Object.keys(e).length === 0;
  };

  const submit = async (ev) => {
    ev.preventDefault();
    if (!validate()) {
      toast.push({ type: "danger", title: "Formulario incompleto", message: "Revisa los campos marcados." });
      return;
    }
    setLoading(true);
    try {
      await login(form.email, form.password);
      toast.push({ type: "success", title: "Bienvenido", message: "Sesión iniciada correctamente." });
      navigate("/dashboard");
    } catch (err) {
      const msg = err.response?.data?.mensaje || "No se pudo iniciar sesión.";
      toast.push({ type: "danger", title: "Acceso denegado", message: msg });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-screen">
      <aside className="login-aside">
        <div>
          <div className="d-flex align-items-center gap-2 mb-5">
            <span className="brand-mark" style={{ width: 44, height: 44, borderRadius: 12, background: "rgba(255,255,255,.14)", display: "inline-flex", alignItems: "center", justifyContent: "center", fontSize: "1.4rem" }}>
              <Icon name="shield-lock" />
            </span>
            <span style={{ fontFamily: "var(--font-display)", fontWeight: 800, fontSize: "1.2rem" }}>room_911</span>
          </div>
          <h1 style={{ fontFamily: "var(--font-display)", fontSize: "2.1rem", fontWeight: 800, lineHeight: 1.15 }}>
            Seguridad dinámica para tu sala restringida
          </h1>
          <p style={{ color: "rgba(255,255,255,.8)", marginTop: 12, maxWidth: 460 }}>
            Sistema de control de acceso por matriz de riesgo y cronograma operativo para laboratorios farmacéuticos.
          </p>
        </div>

        <div>
          {FEATURES.map((f) => (
            <div className="aside-feature" key={f.title}>
              <Icon name={f.icon} />
              <div>
                <h4>{f.title}</h4>
                <p>{f.text}</p>
              </div>
            </div>
          ))}
        </div>

        <div style={{ color: "rgba(255,255,255,.6)", fontSize: "0.8rem" }}>
          © 2026 room_911 · Laboratorio Farmacéutico
        </div>
      </aside>

      <main className="login-main">
        <div className="login-card">
          <div className="login-brand-row d-md-none">
            <span className="brand-mark">
              <Icon name="shield-lock" />
            </span>
            <strong style={{ fontFamily: "var(--font-display)" }}>room_911</strong>
          </div>

          <h2 className="login-title">Iniciar sesión</h2>
          <p className="login-sub">Accede con tu cuenta institucional para continuar.</p>

          <form onSubmit={submit} noValidate>
            <TextField
              id="email"
              label="Correo electrónico"
              type="email"
              icon="envelope"
              placeholder="nombre@laboratorio.com"
              value={form.email}
              onChange={update("email")}
              error={errors.email}
              autoComplete="username"
            />
            <TextField
              id="password"
              label="Contraseña"
              type="password"
              icon="lock"
              placeholder="••••••••"
              value={form.password}
              onChange={update("password")}
              error={errors.password}
              autoComplete="current-password"
            />

            <div className="d-flex align-items-center justify-content-between mb-4">
              <div className="form-check">
                <input className="form-check-input" type="checkbox" id="remember" />
                <label className="form-check-label small text-muted" htmlFor="remember">
                  Recuérdame
                </label>
              </div>
              <button
                type="button"
                className="btn btn-link p-0 small fw-semibold"
                style={{ color: "var(--brand-600)" }}
              >
                ¿Olvidaste tu contraseña?
              </button>
            </div>

            <Button type="submit" size="lg" block loading={loading} icon="box-arrow-in-right">
              Ingresar al sistema
            </Button>
          </form>

          <div className="divider" />
          <p className="small text-muted-2 mb-0">
            <Icon name="info-circle" /> Cuentas de prueba: <code>admin123</code>, <code>guardia123</code>, <code>operario123</code>
          </p>
        </div>
      </main>
    </div>
  );
}
