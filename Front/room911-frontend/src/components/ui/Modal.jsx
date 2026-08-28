import { useEffect } from "react";
import { Icon } from "./Icon";

export function Modal({ open, onClose, title, children, footer, size }) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e) => e.key === "Escape" && onClose?.();
    document.addEventListener("keydown", onKey);
    document.body.style.overflow = "hidden";
    return () => {
      document.removeEventListener("keydown", onKey);
      document.body.style.overflow = "";
    };
  }, [open, onClose]);

  if (!open) return null;

  return (
    <div className="modal-backdrop-r911" onMouseDown={(e) => e.target === e.currentTarget && onClose?.()}>
      <div className={["modal-r911", size ? `modal-${size}` : ""].filter(Boolean).join(" ")} role="dialog" aria-modal="true">
        <div className="modal-header-r911">
          <h5>{title}</h5>
          <button className="modal-close" onClick={onClose} aria-label="Cerrar">
            <Icon name="x-lg" />
          </button>
        </div>
        <div className="modal-body-r911">{children}</div>
        {footer && <div className="modal-footer-r911">{footer}</div>}
      </div>
    </div>
  );
}
