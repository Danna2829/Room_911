export function Icon({ name, className = "", style }) {
  return <i className={`bi bi-${name} ${className}`} style={style} aria-hidden="true" />;
}
