import React, { useState, useEffect, useCallback } from 'react';
import api from '../api/api';

const PanelGuardia = () => {
    const [idUsuario, setIdUsuario] = useState('EMP-8821');
    const [motivo, setMotivo] = useState('INCAPACIDAD');
    const [fechaInicio, setFechaInicio] = useState(new Date().toISOString().slice(0, 16));
    const [fechaFin, setFechaFin] = useState('');
    const [suspensiones, setSuspensiones] = useState([]);
    const [monitorLogs, setMonitorLogs] = useState([]);
    const [mensaje, setMensaje] = useState('');
    const [cargando, setCargando] = useState(false);

    const cargarSuspensiones = useCallback(async () => {
        try {
            const res = await api.get('/guardia/suspensiones');
            setSuspensiones(res.data);
        } catch (err) {
            console.error('Error cargando suspensiones:', err);
        }
    }, []);

    const cargarMonitor = useCallback(async () => {
        try {
            const res = await api.get('/acceso/monitor');
            setMonitorLogs(res.data.reverse()); // Los más recientes primero
        } catch (err) {
            console.error('Error cargando monitor:', err);
        }
    }, []);

    useEffect(() => {
        cargarSuspensiones();
        cargarMonitor();
        const interval = setInterval(cargarMonitor, 3000); // Polling cada 3 segundos
        return () => clearInterval(interval);
    }, [cargarSuspensiones, cargarMonitor]);

    const handleSuspender = async (e) => {
        e.preventDefault();
        if (!idUsuario.trim()) return;
        setCargando(true);
        setMensaje('');

        try {
            await api.post('/guardia/suspender', {
                idUsuario: idUsuario.trim(),
                motivo,
                fechaInicio: fechaInicio ? new Date(fechaInicio).toISOString() : null,
                fechaFin: fechaFin ? new Date(fechaFin).toISOString() : null
            });
            setMensaje(`✅ Permisos suspendidos para el usuario ${idUsuario}`);
            cargarSuspensiones();
        } catch (err) {
            console.error('Error al registrar suspensión:', err);
            setMensaje('❌ Error al suspender permiso');
        } finally {
            setCargando(false);
        }
    };

    const handleDesactivar = async (id) => {
        try {
            await api.put(`/guardia/suspensiones/${id}/desactivar`);
            cargarSuspensiones();
        } catch (err) {
            console.error('Error al desactivar suspensión:', err);
        }
    };

    return (
        <div className="module-container">
            <div className="module-card">
                <h2>🛡️ Panel de Control de Seguridad (Guardia / Celador)</h2>
                <p>Gestión inmediata de suspensiones individuales y monitoreo en tiempo real</p>

                <div className="grid-dos-columnas">
                    {/* Formulario de Suspensión */}
                    <div className="seccion-suspension">
                        <h3>🚫 Registrar Suspensión de Permisos</h3>
                        <form onSubmit={handleSuspender} className="form-vertical">
                            <div className="form-group">
                                <label>ID Interno de Expediente:</label>
                                <input 
                                    type="text" 
                                    value={idUsuario} 
                                    onChange={(e) => setIdUsuario(e.target.value.toUpperCase())}
                                    placeholder="ej. EMP-8821"
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Motivo de Suspensión:</label>
                                <select value={motivo} onChange={(e) => setMotivo(e.target.value)}>
                                    <option value="INCAPACIDAD">INCAPACIDAD MÉDICA</option>
                                    <option value="SANCIÓN">SANCIÓN DISCIPLINARIA</option>
                                    <option value="CAMBIO_TURNO">CAMBIO DE TURNO</option>
                                    <option value="OTRO">OTRO</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label>Fecha y Hora Inicio:</label>
                                <input 
                                    type="datetime-local" 
                                    value={fechaInicio} 
                                    onChange={(e) => setFechaInicio(e.target.value)}
                                />
                            </div>

                            <div className="form-group">
                                <label>Fecha y Hora Fin (Opcional):</label>
                                <input 
                                    type="datetime-local" 
                                    value={fechaFin} 
                                    onChange={(e) => setFechaFin(e.target.value)}
                                />
                            </div>

                            <button type="submit" className="btn-danger" disabled={cargando}>
                                {cargando ? 'Procesando...' : 'Suspender Permisos Inmediatamente'}
                            </button>
                        </form>

                        {mensaje && <div className="info-mensaje">{mensaje}</div>}

                        {/* Listado de Suspensiones Activas */}
                        <h4>Suspensiones Registradas</h4>
                        <div className="tabla-scroll">
                            <table className="tabla-estandar">
                                <thead>
                                    <tr>
                                        <th>Usuario</th>
                                        <th>Motivo</th>
                                        <th>Estado</th>
                                        <th>Acción</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {suspensiones.length === 0 ? (
                                        <tr><td colSpan="4">No hay suspensiones registradas</td></tr>
                                    ) : (
                                        suspensiones.map(s => (
                                            <tr key={s.id}>
                                                <td><strong>{s.idUsuario}</strong></td>
                                                <td>{s.motivo}</td>
                                                <td>
                                                    <span className={`tag-${s.activo ? 'activo' : 'inactivo'}`}>
                                                        {s.activo ? 'ACTIVA' : 'INACTIVA'}
                                                    </span>
                                                </td>
                                                <td>
                                                    {s.activo && (
                                                        <button 
                                                            onClick={() => handleDesactivar(s.id)}
                                                            className="btn-secundario-sm"
                                                        >
                                                            Reactivar
                                                        </button>
                                                    )}
                                                </td>
                                            </tr>
                                        ))
                                    )}
                                </tbody>
                            </table>
                        </div>
                    </div>

                    {/* Monitor en Tiempo Real */}
                    <div className="seccion-monitor">
                        <h3>📡 Monitor de Accesos a room_911 (Tiempo Real)</h3>
                        <div className="monitor-feed">
                            {monitorLogs.length === 0 ? (
                                <p className="feed-vacio">Esperando intentos de acceso...</p>
                            ) : (
                                monitorLogs.slice(0, 10).map((log, idx) => (
                                    <div key={log.id || idx} className={`item-feed ${log.resultado === 'PERMITIDO' ? 'feed-permitido' : 'feed-denegado'}`}>
                                        <div className="feed-header">
                                            <span className="feed-id">{log.idUsuario}</span>
                                            <span className="feed-evento">{log.tipoEvento}</span>
                                            <span className="feed-badge">{log.resultado}</span>
                                        </div>
                                        {log.motivoRechazo && (
                                            <div className="feed-motivo">{log.motivoRechazo}</div>
                                        )}
                                        {log.tareaAlternativaAsignada && (
                                            <div className="feed-tarea">🔄 {log.tareaAlternativaAsignada}</div>
                                        )}
                                        <div className="feed-time">
                                            {new Date(log.timestamp).toLocaleTimeString()} - {new Date(log.timestamp).toLocaleDateString()}
                                        </div>
                                    </div>
                                ))
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default PanelGuardia;
