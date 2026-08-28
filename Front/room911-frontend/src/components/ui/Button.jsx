import { Icon } from "./Icon";

const VARIANT = {
  primary: "btn-primary",
  secondary: "btn-secondary",
  success: "btn-success",
  danger: "btn-danger",
  warning: "btn-warning",
  info: "btn-info",
  light: "btn-light",
  dark: "btn-dark-soft",
  soft: "btn-soft",
  outline: "btn-outline-primary",
  link: "btn-link",
};

const SIZE = { sm: "btn-sm", lg: "btn-lg" };

export function Button({
  variant = "primary",
  size,
  icon,
  loading = false,
  block = false,
  children,
  className = "",
  type = "button",
  ...props
}) {
  const cls = [
    "btn",
    VARIANT[variant] || "btn-primary",
    size ? SIZE[size] : "",
    block ? "w-100" : "",
    className,
  ]
    .filter(Boolean)
    .join(" ");

  return (
    <button className={cls} type={type} disabled={loading || props.disabled} {...props}>
      {loading && <span className="spinner-border spinner-border-sm" />}
      {!loading && icon && <Icon name={icon} />}
      {children}
    </button>
  );
}
