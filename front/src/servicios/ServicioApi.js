const API_URL = process.env.REACT_APP_API_URL || "http://localhost:8080/api";
async function pedir(ruta, opciones = {}) { const respuesta = await fetch(`${API_URL}${ruta}`, { headers: { "Content-Type": "application/json", ...(opciones.headers || {}) }, ...opciones }); const texto = await respuesta.text(); let datos = {}; try { datos = texto ? JSON.parse(texto) : {}; } catch { datos = {}; } if (!respuesta.ok) throw new Error(datos.mensaje || "No fue posible completar la solicitud."); return datos; }
export const identificarUsuario = idUsuario => pedir("/usuarios/identificar", { method: "POST", body: JSON.stringify({ idUsuario }) });
export const listarUsuarios = () => pedir("/usuarios");
export const listarPerfiles = () => pedir("/perfiles");
export const crearUsuario = datos => pedir("/usuarios", { method: "POST", body: JSON.stringify(datos) });
export const cambiarEstadoUsuario = (id, activo, desde = "", hasta = "") => pedir(`/usuarios/${id}/estado?activo=${activo}${desde ? `&desde=${desde}` : ""}${hasta ? `&hasta=${hasta}` : ""}`, { method: "PATCH" });
export const listarCronogramas = () => pedir("/cronogramas");
export const listarMedicamentos = () => pedir("/medicamentos");
export const crearCronograma = datos => pedir("/cronogramas", { method: "POST", body: JSON.stringify(datos) });
export const evaluarAcceso = datos => pedir("/accesos/evaluar", { method: "POST", body: JSON.stringify(datos) });
export const listarAccesos = () => pedir("/accesos");
export const exportarAccesos = async (filtros = {}) => { const query = new URLSearchParams(Object.entries(filtros).filter(([, valor]) => valor)); const respuesta = await fetch(`${API_URL}/accesos/exportar.csv${query.toString() ? `?${query}` : ""}`); if (!respuesta.ok) throw new Error("No fue posible exportar la auditoría."); return respuesta.blob(); };
