import React, { useEffect, useState } from "react";
import api from "../../api/api";

function ListaUsuarios() {
    const [usuarios, setUsuarios] = useState([]);
    const [error, setError] = useState(null);

    const cargarUsuarios = () => {
        api.get("/admin/listar-usuarios")
            .then(res => {
                setUsuarios(res.data);
                setError(null);
            })
            .catch(err => {
                console.error("Error al cargar usuarios:", err);
                setError(err.response?.data?.message || "Error al conectar con el servidor.");
            });
    };

    useEffect(() => {
        cargarUsuarios();
    }, []);

    return (
        <div style={{ padding: "15px", border: "1px solid #ccc", borderRadius: "8px" }}>
            <h3>Lista de Usuarios Registrados</h3>
            {error && <div style={{ color: "red", marginBottom: "10px" }}>⚠️ {error}</div>}
            <table border="1" cellPadding="8" style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                    <tr style={{ backgroundColor: "#f1f5f9" }}>
                        <th>ID Expediente</th>
                        <th>Nombre</th>
                        <th>Apellido</th>
                        <th>Correo</th>
                        <th>Rol</th>
                        <th>Estado</th>
                    </tr>
                </thead>
                <tbody>
                    {usuarios.length === 0 ? (
                        <tr><td colSpan="6" style={{ textAlign: "center" }}>No hay usuarios registrados</td></tr>
                    ) : (
                        usuarios.map(u => (
                            <tr key={u.idUsuario}>
                                <td><strong>{u.idUsuario}</strong></td>
                                <td>{u.nombre}</td>
                                <td>{u.apellido}</td>
                                <td>{u.correo}</td>
                                <td>{u.rol}</td>
                                <td>{u.estado ? "ACTIVO" : "INACTIVO"}</td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
}

export default ListaUsuarios;
