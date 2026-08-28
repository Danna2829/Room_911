import React, { useState, useEffect, useCallback } from 'react';
import api from '../api/api';

const PanelReportes = () => {
    const [reportes, setReportes] = useState([]);
    const [filtroResultado, setFiltroResultado] = useState('TODOS');
    const [filtroTexto, setFiltroTexto] = useState('');
    const [cargando, setCargando] = useState(false);

    const cargarReportes = useCallback(async () => {
        setCargando(true);
        try {
            const res = await api.get('/reportes/accesos');
            setReportes(res.data);
        } catch (err) {
            console.error('Error cargando reportes:', err);
        } finally {
            setCargando(false);
        }
    }, []);

    useEffect(() => {
        cargarReportes();
    }, [cargarReportes]);

    const handleDescargarCSV = () => {
        window.open('http://localhost:8080/api/reportes/exportar/csv', '_blank');
    };

    const logsFiltrados = reportes.filter(log => {
        const coincideResultado = filtroResultado === 'TODOS' || log.resultado === filtroResultado;
        const coincideTexto = !filtroTexto || 
            (log.idUsuario && log.idUsuario.toLowerCase().includes(filtroTexto.toLowerCase())) ||
            (log.tipoEvento && log.tipoEvento.toLowerCase().includes(filtroTexto.toLowerCase())) ||
            (log.motivoRechazo && log.motivoRechazo.toLowerCase().includes(filtroTexto.toLowerCase())) ||
            (log.tareaAlternativaAsignada && log.tareaAlternativaAsignada.toLowerCase().includes(filtroTexto.toLowerCase()));
        return coincideResultado && coincideTexto;
    });

    return (
        <div className="module-container">
            <div className="module-card">
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '15px' }}>
                    <div>
                        <h2>📊 Auditoría de Seguridad y Reportes</h2>
                        <p>Trazabilidad inmutable de eventos de acceso a room_911 y plan de contingencia</p>
                    </div>
                    <div style={{ display: 'flex', gap: '10px' }}>
                        <button onClick={cargarReportes} className="btn-secondary" disabled={cargando}>
                            🔄 {cargando ? 'Actualizando...' : 'Refrescar'}
                        </button>
                        <button onClick={handleDescargarCSV} className="btn-primary" style={{ backgroundColor: '#059669' }}>
                            📥 Descargar Reporte CSV
                        </button>
                    </div>
                </div>

                <div className="filtro-box" style={{ marginTop: '20px', display: 'flex', gap: '15px', flexWrap: 'wrap' }}>
                    <input 
                        type="text"
                        value={filtroTexto}
                        onChange={(e) => setFiltroTexto(e.target.value)}
                        placeholder="🔍 Buscar por expediente, evento o motivo..."
                        className="input-filtro"
                        style={{ flex: 1, minWidth: '240px' }}
                    />
                    <select 
                        value={filtroResultado} 
                        onChange={(e) => setFiltroResultado(e.target.value)}
                        style={{ padding: '10px', borderRadius: '6px', border: '1px solid #cbd5e1', fontWeight: 'bold' }}
                    >
                        <option value="TODOS">Todos los Resultados</option>
                        <option value="PERMITIDO">🟢 Solo PERMITIDOS</option>
                        <option value="DENEGADO">🔴 Solo DENEGADOS</option>
                    </select>
                </div>

                <div className="tabla-scroll" style={{ marginTop: '15px', maxHeight: '500px', overflowY: 'auto' }}>
                    <table className="tabla-estandar">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Expediente</th>
                                <th>Fecha / Hora</th>
                                <th>Evento</th>
                                <th>Resultado</th>
                                <th>Motivo de Rechazo</th>
                                <th>Tarea Alternativa (Contingencia)</th>
                            </tr>
                        </thead>
                        <tbody>
                            {logsFiltrados.length === 0 ? (
                                <tr><td colSpan="7" style={{ textAlign: 'center', padding: '20px' }}>No hay registros de auditoría disponibles</td></tr>
                            ) : (
                                logsFiltrados.map((log) => (
                                    <tr key={log.id}>
                                        <td><strong>{log.id}</strong></td>
                                        <td><span className="badge-expediente">{log.idUsuario}</span></td>
                                        <td style={{ fontSize: '12px', whiteSpace: 'nowrap' }}>
                                            {log.timestamp ? new Date(log.timestamp).toLocaleString() : '-'}
                                        </td>
                                        <td><strong>{log.tipoEvento}</strong></td>
                                        <td>
                                            <span className={`tag-${log.resultado === 'PERMITIDO' ? 'activo' : 'inactivo'}`}>
                                                {log.resultado}
                                            </span>
                                        </td>
                                        <td style={{ fontSize: '13px', color: log.motivoRechazo ? '#b91c1c' : '#64748b' }}>
                                            {log.motivoRechazo || 'N/A'}
                                        </td>
                                        <td style={{ fontSize: '13px', color: '#047857' }}>
                                            {log.tareaAlternativaAsignada ? `📋 ${log.tareaAlternativaAsignada}` : '-'}
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    );
};

export default PanelReportes;
