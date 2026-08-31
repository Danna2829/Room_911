import React, { useState } from "react";
import { identificarUsuario } from "./servicios/ServicioApi";
import Aplicacion911 from "./componentes/Aplicacion911";
import "./estilos/aplicacion.css";

export default function App() {
  const [usuario, setUsuario] = useState(() => { try { return JSON.parse(localStorage.getItem("room911_usuario")) || null; } catch { return null; } });
  const [error, setError] = useState("");
  const ingresar = async (evento) => {
    evento.preventDefault(); setError(""); const id = evento.currentTarget.idUsuario.value.trim().toUpperCase();
    if (!id) return setError("Ingresa tu ID interno para continuar.");
    try { const datos = await identificarUsuario(id); localStorage.setItem("room911_usuario", JSON.stringify(datos)); setUsuario(datos); } catch (e) { setError(e.message); }
  };
  if (!usuario) return <main className="pantalla-login"><section className="tarjeta-login"><div className="marca"><span>911</span><div><strong>ROOM_911</strong><small>CONTROL DE ACCESO</small></div></div><div className="login-copia"><p className="eyebrow">ÁREA RESTRINGIDA</p><h1>Identifica tu acceso</h1><p>Usa tu ID interno para validar el turno, el nivel de riesgo y el cronograma activo.</p></div><form onSubmit={ingresar} className="formulario-login">{error && <div className="alerta error">{error}</div>}<label htmlFor="idUsuario">ID interno</label><input id="idUsuario" name="idUsuario" placeholder="Ej. EMP-301" autoComplete="off" autoFocus/><button className="boton primario" type="submit">Validar identidad <span>→</span></button></form><p className="nota-seguridad">● Simulación sin contraseña · Trazabilidad activa</p></section><aside className="login-lateral"><div><p className="eyebrow">MATRIZ ABAC</p><h2>La puerta responde al contexto.</h2><p>Perfil + cronograma + estado operativo. Cada decisión queda registrada.</p></div><div className="mini-estadisticas"><span><b>03</b><small>niveles de acceso</small></span><span><b>24/7</b><small>auditoría</small></span></div></aside></main>;
  return <Aplicacion911 usuario={usuario} alSalir={() => { localStorage.removeItem("room911_usuario"); setUsuario(null); }} />;
}
