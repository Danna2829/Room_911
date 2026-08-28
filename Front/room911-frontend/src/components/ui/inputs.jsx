export function TextField({ label, icon, error, id, className = "", ...props }) {
  const input = (
    <input
      id={id}
      className={["form-control", error ? "is-invalid" : "", className].filter(Boolean).join(" ")}
      {...props}
    />
  );
  return (
    <div className="mb-3">
      {label && (
        <label htmlFor={id} className="form-label">
          {label}
        </label>
      )}
      {icon ? <div className="input-icon">{input}</div> : input}
      {error && <div className="field-error">{error}</div>}
    </div>
  );
}

export function SelectField({ label, error, options = [], id, className = "", ...props }) {
  return (
    <div className="mb-3">
      {label && (
        <label htmlFor={id} className="form-label">
          {label}
        </label>
      )}
      <select id={id} className={["form-select", className].filter(Boolean).join(" ")} {...props}>
        {options.map((o) => (
          <option key={o.value} value={o.value}>
            {o.label}
          </option>
        ))}
      </select>
      {error && <div className="field-error">{error}</div>}
    </div>
  );
}

export function Slider({ label, value, min = 0, max = 100, step = 1, onChange, ...props }) {
  return (
    <div className="mb-3">
      {label && (
        <div className="d-flex justify-content-between align-items-center">
          <label className="form-label mb-1">{label}</label>
          <span className="text-muted small fw-semibold">{value}</span>
        </div>
      )}
      <input
        type="range"
        className="form-range"
        min={min}
        max={max}
        step={step}
        value={value}
        onChange={onChange}
        {...props}
      />
    </div>
  );
}
