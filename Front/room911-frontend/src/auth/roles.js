// Configuracion central de roles: navegacion por panel y destino tras el login.
export const ROLES = {
  SUPERADMINISTRADOR: "SUPERADMINISTRADOR",
  ADMINISTRADOR: "ADMINISTRADOR",
  GUARDIA_SEGURIDAD: "GUARDIA_SEGURIDAD",
  SECRETARIA: "SECRETARIA",
  OPERARIO: "OPERARIO",
};

// A donde llega cada rol justo despues de iniciar sesion.
export const HOME_BY_ROL = {
  SUPERADMINISTRADOR: "/dashboard",
  ADMINISTRADOR: "/dashboard",
  SECRETARIA: "/cronograma",
  GUARDIA_SEGURIDAD: "/monitor",
  OPERARIO: "/garita",
};

export const homeFor = (rol) => HOME_BY_ROL[rol] || "/dashboard";

// Todas las secciones del sidebar; cada item declara los roles autorizados.
export const NAV_SECTIONS = [
  {
    label: "Operación",
    items: [
      { to: "/dashboard", label: "Panel General", icon: "speedometer2", roles: ["SUPERADMINISTRADOR", "ADMINISTRADOR", "SECRETARIA"] },
      { to: "/garita", label: "Garita / Torniquete", icon: "door-closed", roles: ["SUPERADMINISTRADOR", "ADMINISTRADOR", "GUARDIA_SEGURIDAD", "OPERARIO"] },
      { to: "/cronograma", label: "Cronograma", icon: "calendar3", roles: ["SUPERADMINISTRADOR", "ADMINISTRADOR", "SECRETARIA"] },
      { to: "/monitor", label: "Monitor en Vivo", icon: "activity", roles: ["SUPERADMINISTRADOR", "ADMINISTRADOR", "GUARDIA_SEGURIDAD"] },
    ],
  },
  {
    label: "Gestión",
    items: [
      { to: "/usuarios", label: "Usuarios", icon: "people", roles: ["SUPERADMINISTRADOR", "ADMINISTRADOR"] },
      { to: "/medicamentos", label: "Medicamentos", icon: "capsule", roles: ["SUPERADMINISTRADOR", "ADMINISTRADOR", "SECRETARIA"] },
      { to: "/inventario", label: "Inventario", icon: "box-seam", roles: ["SUPERADMINISTRADOR", "ADMINISTRADOR", "SECRETARIA"] },
      { to: "/reportes", label: "Reportes & Auditoría", icon: "clipboard-data", roles: ["SUPERADMINISTRADOR", "ADMINISTRADOR", "GUARDIA_SEGURIDAD", "SECRETARIA"] },
    ],
  },
];

export const canAccess = (rol, path) =>
  NAV_SECTIONS.some((sec) =>
    sec.items.some((item) => item.to === path && item.roles.includes(rol))
  );

export const sectionsFor = (rol) =>
  NAV_SECTIONS.map((sec) => ({
    ...sec,
    items: sec.items.filter((item) => item.roles.includes(rol)),
  })).filter((sec) => sec.items.length > 0);
