import React, { useState, useEffect, useCallback } from 'react';
import api from '../api/api';

const AdminUsuarios = () => {
    const [usuarios, setUsuarios] = useState([]);
    const [filtro, setFiltro] = useState('');
    const [nombre, setNombre] = useState('');
    const [apellido, setApellido] = useState('');
    const [correo, setCorreo] = useState('');
    const [rol, setRol] = useState('OPERARIO');
    const [nivelAcceso, setNivelAcceso] = useState(1);
    const [contrasena, setContrasena] = useState('pass123');
    const [mensaje, setMensaje] = useState('');
    const [cargando, setCargando] = useState(false);

    const cargarUsuarios = useCallback(async () => {
        try {
            const res = await api.get('/admin/listar-usuarios');
            setUsuarios(res.data);
        } catch (err) {
            console.error('Error cargando usuarios:', err);
        }
    }, []);

    useEffect(() => {
        cargarUsuarios();
    }, [cargarUsuarios]);

    const handleCrearUsuario = async (e) => {
        e.preventDefault();
        if (!nombre.trim() || !apellido.trim() || !correo.trim()) {
            setMensaje('❌ Todos los campos son obligatorios');
            return;
        }
        setCargando(true);
        setMensaje('');

        try {
            const nuevoUsuario = {
                nombre: nombre.trim(),
                apellido: apellido.trim(),
                correo: correo.trim(),
                rol,
                nivelAcceso: rol === 'OPERARIO' ? nivelAcceso : (rol === 'ADMINISTRADOR' || rol === 'GUARDIA_SEGURIDAD' ? 3 : null),
                contraseña: contrasena,
                estado: true
            };
            const res = await api.post('/admin/crear-usuario', nuevoUsuario);
            setMensaje(`✅ Usuario creado correctamente con ID: ${res.data.idUsuario}`);
            setNombre('');
            setApellido('');
            setCorreo('');
            setContrasena('pass123');
            cargarUsuarios();
        } catch (err) {
            console.error('Error creando usuario:', err);
            const serverMsg = err.response?.data?.message || err.response?.data?.mensaje || 'Error al crear usuario. Verifique si el correo ya existe.';
            setMensaje(`❌ ${serverMsg}`);
        } finally {
            setCargando(false);
        }
    };

    const handleToggleEstado = async (u) => {
        const accion = u.estado ? 'inhabilitar' : 'reactivar';
        if (!window.confirm(`¿Desea ${accion} lógicamente el usuario ${u.idUsuario}? (No se borrará ningún dato de la base de datos)`)) return;
        try {
            const nuevoEstado = !u.estado;
            await api.put(`/admin/editar-usuario/${u.idUsuario}`, {
                ...u,
                estado: nuevoEstado
            });
            cargarUsuarios();
        } catch (err) {
            console.error('Error actualizando estado:', err);
            alert('Error actualizando el estado del usuario');
        }
    };

    const usuariosFiltrados = usuarios.filter(u => 
        (u.idUsuario && u.idUsuario.toLowerCase().includes(filtro.toLowerCase())) ||
        (u.nombre && u.nombre.toLowerCase().includes(filtro.toLowerCase())) ||
        (u.apellido && u.apellido.toLowerCase().includes(filtro.toLowerCase())) ||
        (u.correo && u.correo.toLowerCase().includes(filtro.toLowerCase())) ||
        (u.rol && u.rol.toLowerCase().includes(filtro.toLowerCase()))
    );

    return (
        <div className="module-container">
            <div className="module-card">
                <h2>👤 Gestión de Usuarios y Asignación de Niveles</h2>
                <p>Módulo Administrador para alta, consulta, asignación ABAC e inhabilitación lógica (Soft Delete)</p>

                <div className="grid-dos-columnas">
                    {/* Formulario de Crear Usuario */}
                    <div className="seccion-formulario">
                        <h3>➕ Registrar Nuevo Usuario</h3>
                        <form onSubmit={handleCrearUsuario} className="form-vertical">
                            <div className="form-group">
                                <label>Nombre:</label>
                                <input 
                                    type="text" 
                                    value={nombre} 
                                    onChange={(e) => setNombre(e.target.value)} 
                                    placeholder="ej. Carlos"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Apellido:</label>
                                <input 
                                    type="text" 
                                    value={apellido} 
                                    onChange={(e) => setApellido(e.target.value)} 
                                    placeholder="ej. Mendoza"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Correo Electrónico:</label>
                                <input 
                                    type="email" 
                                    value={correo} 
                                    onChange={(e) => setCorreo(e.target.value)} 
                                    placeholder="ej. carlos@farmaceutica.com"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Rol de Usuario:</label>
                                <select value={rol} onChange={(e) => setRol(e.target.value)}>
                                    <option value="OPERARIO">OPERARIO</option>
                                    <option value="GUARDIA_SEGURIDAD">GUARDIA_SEGURIDAD</option>
                                    <option value="SECRETARIA">SECRETARIA</option>
                                    <option value="ADMINISTRADOR">ADMINISTRADOR</option>
                                </select>
                            </div>

                            {rol === 'OPERARIO' && (
                                <div className="form-group">
                                    <label>Nivel de Acceso Operario (ABAC):</label>
                                    <select value={nivelAcceso} onChange={(e) => setNivelAcceso(parseInt(e.target.value))}>
                                        <option value={1}>Nivel 1 (Medicamentos Tipo 1 y 2)</option>
                                        <option value={2}>Nivel 2 (Medicamentos Tipo 2 y 5)</option>
                                        <option value={3}>Nivel 3 (Acceso Global - Incluye Tipo 4 Restringido)</option>
                                    </select>
                                </div>
                            )}

                            <div className="form-group">
                                <label>Contraseña Web:</label>
                                <input 
                                    type="password" 
                                    value={contrasena} 
                                    onChange={(e) => setContrasena(e.target.value)} 
                                    required
                                />
                            </div>

                            <button type="submit" className="btn-primary" disabled={cargando}>
                                {cargando ? 'Guardando...' : 'Registrar Usuario (Generar EMP-XXXX)'}
                            </button>
                        </form>

                        {mensaje && <div className="info-mensaje">{mensaje}</div>}
                    </div>

                    {/* Listado de Usuarios */}
                    <div className="seccion-tabla">
                        <h3>📋 Listado de Usuarios Registrados ({usuariosFiltrados.length})</h3>
                        
                        <div className="filtro-box">
                            <input 
                                type="text" 
                                value={filtro}
                                onChange={(e) => setFiltro(e.target.value)}
                                placeholder="🔍 Buscar por ID, Nombre, Correo o Rol..."
                                className="input-filtro"
                            />
                        </div>

                        <div className="tabla-scroll">
                            <table className="tabla-estandar">
                                <thead>
                                    <tr>
                                        <th>ID Expediente</th>
                                        <th>Nombre Completo</th>
                                        <th>Correo</th>
                                        <th>Rol</th>
                                        <th>Nivel ABAC</th>
                                        <th>Estado</th>
                                        <th>Inhabilitar / Reactivar</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {usuariosFiltrados.length === 0 ? (
                                        <tr><td colSpan="7">No se encontraron usuarios</td></tr>
                                    ) : (
                                        usuariosFiltrados.map(u => (
                                            <tr key={u.idUsuario}>
                                                <td><span className="badge-expediente">{u.idUsuario}</span></td>
                                                <td>{u.nombre} {u.apellido}</td>
                                                <td>{u.correo}</td>
                                                <td><span className="badge-rol">{u.rol}</span></td>
                                                <td>
                                                    {u.nivelAcceso ? (
                                                        <span className={`badge-nivel badge-nivel-${u.nivelAcceso}`}>
                                                            Nivel {u.nivelAcceso}
                                                        </span>
                                                    ) : (
                                                        <span style={{ color: '#888' }}>-</span>
                                                    )}
                                                </td>
                                                <td>
                                                    <span className={`tag-${u.estado ? 'activo' : 'inactivo'}`}>
                                                        {u.estado ? 'ACTIVO' : 'INACTIVO'}
                                                    </span>
                                                </td>
                                                <td>
                                                    <button 
                                                        onClick={() => handleToggleEstado(u)}
                                                        style={{ 
                                                            background: u.estado ? '#fee2e2' : '#dcfce7', 
                                                            color: u.estado ? '#dc2626' : '#166534', 
                                                            border: `1px solid ${u.estado ? '#f87171' : '#86efac'}`, 
                                                            borderRadius: '4px', 
                                                            padding: '4px 8px', 
                                                            cursor: 'pointer', 
                                                            fontSize: '12px',
                                                            fontWeight: 'bold'
                                                        }}
                                                        title={u.estado ? "Inhabilitar usuario lógicamente" : "Reactivar usuario"}
                                                    >
                                                        {u.estado ? '🔒 Desactivar' : '🔓 Reactivar'}
                                                    </button>
                                                </td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default AdminUsuarios;
