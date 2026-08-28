import React, { useState } from "react";
import api from "../../api/api";

function CrearUsuario({ onUsuarioCreado }) {
    const [form, setForm] = useState({ nombre: "", apellido: "", correo: "", rol: "OPERARIO", contrasena: "" });
    const [error, setError] = useState(null);

    const handleChange = e => {
        const name = e.target.name === "contraseña" ? "contrasena" : e.target.name;
        setForm({ ...form, [name]: e.target.value });
    };

    const handleCrear = async (e) => {
        if (e) e.preventDefault();
        setError(null);
        try {
            const payload = {
                ...form,
                contraseña: form.contrasena
            };
            const response = await api.post("/admin/crear-usuario", payload);
            alert("Usuario creado correctamente con ID: " + response.data.idUsuario);
            if (onUsuarioCreado) onUsuarioCreado();
        } catch (err) {
            console.error("Error al crear usuario:", err);
            const msg = err.response?.data?.message || err.response?.data?.mensaje || "Error al crear el usuario en el servidor.";
            setError(msg);
        }
    };

    return (
        <div style={{ padding: "15px", border: "1px solid #ccc", borderRadius: "8px", marginBottom: "20px" }}>
            <h3>Crear Nuevo Usuario</h3>
            {error && <div style={{ color: "red", marginBottom: "10px" }}>⚠️ {error}</div>}
            <form onSubmit={handleCrear}>
                <div style={{ marginBottom: "10px" }}>
                    <input name="nombre" placeholder="Nombre" onChange={handleChange} required style={{ width: "100%", padding: "8px" }} />
                </div>
                <div style={{ marginBottom: "10px" }}>
                    <input name="apellido" placeholder="Apellido" onChange={handleChange} required style={{ width: "100%", padding: "8px" }} />
                </div>
                <div style={{ marginBottom: "10px" }}>
                    <input name="correo" type="email" placeholder="Correo electrónico" onChange={handleChange} required style={{ width: "100%", padding: "8px" }} />
                </div>
                <div style={{ marginBottom: "10px" }}>
                    <select name="rol" onChange={handleChange} style={{ width: "100%", padding: "8px" }}>
                        <option value="OPERARIO">OPERARIO</option>
                        <option value="GUARDIA_SEGURIDAD">GUARDIA_SEGURIDAD</option>
                        <option value="SECRETARIA">SECRETARIA</option>
                        <option value="ADMINISTRADOR">ADMINISTRADOR</option>
                    </select>
                </div>
                <div style={{ marginBottom: "10px" }}>
                    <input name="contrasena" type="password" placeholder="Contraseña" onChange={handleChange} required style={{ width: "100%", padding: "8px" }} />
                </div>
                <button type="submit" style={{ padding: "10px 20px", backgroundColor: "#1e3a8a", color: "#fff", border: "none", borderRadius: "4px", cursor: "pointer" }}>
                    Crear Usuario
                </button>
            </form>
        </div>
    );
}

export default CrearUsuario;
