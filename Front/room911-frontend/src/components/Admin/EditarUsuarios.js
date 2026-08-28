import React, { useState } from "react";
import api from "../../api/api";

function EditarUsuarios({ usuario, onGuardado, onCancelar }) {
    const [nombre, setNombre] = useState(usuario?.nombre || "");
    const [apellido, setApellido] = useState(usuario?.apellido || "");
    const [correo, setCorreo] = useState(usuario?.correo || "");
    const [rol, setRol] = useState(usuario?.rol || "OPERARIO");
    const [error, setError] = useState(null);

    const handleGuardar = async (e) => {
        e.preventDefault();
        try {
            await api.put(`/admin/editar-usuario/${usuario.idUsuario}`, {
                nombre,
                apellido,
                correo,
                rol
            });
            if (onGuardado) onGuardado();
        } catch (err) {
            console.error("Error al editar usuario:", err);
            setError(err.response?.data?.message || "Error al actualizar el usuario.");
        }
    };

    return (
        <div style={{ padding: "15px", border: "1px solid #1e3a8a", borderRadius: "8px", backgroundColor: "#f8fafc" }}>
            <h4>Editar Usuario: {usuario?.idUsuario}</h4>
            {error && <div style={{ color: "red" }}>⚠️ {error}</div>}
            <form onSubmit={handleGuardar}>
                <div style={{ marginBottom: "8px" }}>
                    <label>Nombre: </label>
                    <input value={nombre} onChange={e => setNombre(e.target.value)} required />
                </div>
                <div style={{ marginBottom: "8px" }}>
                    <label>Apellido: </label>
                    <input value={apellido} onChange={e => setApellido(e.target.value)} required />
                </div>
                <div style={{ marginBottom: "8px" }}>
                    <label>Correo: </label>
                    <input value={correo} onChange={e => setCorreo(e.target.value)} required />
                </div>
                <div style={{ marginBottom: "8px" }}>
                    <label>Rol: </label>
                    <select value={rol} onChange={e => setRol(e.target.value)}>
                        <option value="OPERARIO">OPERARIO</option>
                        <option value="GUARDIA_SEGURIDAD">GUARDIA_SEGURIDAD</option>
                        <option value="SECRETARIA">SECRETARIA</option>
                        <option value="ADMINISTRADOR">ADMINISTRADOR</option>
                    </select>
                </div>
                <button type="submit" style={{ marginRight: "10px" }}>Guardar Cambios</button>
                <button type="button" onClick={onCancelar}>Cancelar</button>
            </form>
        </div>
    );
}

export default EditarUsuarios;
