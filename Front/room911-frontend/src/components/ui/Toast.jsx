import { createContext, useContext, useState, useCallback } from "react";
import { Icon } from "./Icon";

const ToastContext = createContext(null);

const ICON = {
  success: "check-circle-fill",
  danger: "x-circle-fill",
  warning: "exclamation-triangle-fill",
  info: "info-circle-fill",
};

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const dismiss = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const push = useCallback(
    (toast) => {
      const id = Date.now() + Math.random();
      const item = {
        id,
        type: "info",
        title: "",
        message: "",
        duration: 4000,
        ...toast,
      };
      setToasts((prev) => [...prev, item]);
      if (item.duration) setTimeout(() => dismiss(id), item.duration);
      return id;
    },
    [dismiss]
  );

  return (
    <ToastContext.Provider value={{ push, dismiss }}>
      {children}
      <div className="toast-stack">
        {toasts.map((t) => (
          <div key={t.id} className={`toast-r911 toast-${t.type}`} role="status">
            <Icon name={ICON[t.type] || ICON.info} className="toast-icon" />
            <div style={{ flex: 1 }}>
              {t.title && <div className="toast-title">{t.title}</div>}
              {t.message && <div className="toast-msg">{t.message}</div>}
            </div>
            <button className="toast-close" onClick={() => dismiss(t.id)} aria-label="Cerrar">
              <Icon name="x" />
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast debe usarse dentro de <ToastProvider>");
  return ctx;
}
