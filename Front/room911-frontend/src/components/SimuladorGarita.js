import React, { useState } from 'react';
import api from '../api/api';

const SimuladorGarita = () => {
    const [idUsuario, setIdUsuario] = useState('EMP-8821');
    const [tipoEvento, setTipoEvento] = useState('ENTRADA');
    const [resultado, setResultado] = useState(null);
    const [cargando, setCargando] = useState(false);
    const [error, setError] = useState('');

    const handleEvaluar = async (e) => {
        if (e) e.preventDefault();
        if (!idUsuario.trim()) {
            setError('Ingrese un ID de expediente válido (ej. EMP-8821)');
            return;
        }
        setError('');
        setCargando(true);

        try {
            const res = await api.post('/acceso/evaluar', {
                idUsuario: idUsuario.trim(),
                tipoEvento
            });
            setResultado(res.data);
        } catch (err) {
            console.error('Error al evaluar acceso:', err);
            setError('Error de conexión con la garita backend');
        } finally {
            setCargando(false);
        }
    };

    const handleTeclado = (val) => {
        if (val === 'CLEAR') {
            setIdUsuario('');
        } else if (val === 'BACK') {
            setIdUsuario(prev => prev.slice(0, -1));
        } else {
            setIdUsuario(prev => prev + val);
        }
    };

    return (
        <div className="garita-container">
            <div className="garita-card">
                <div className="garita-header">
                    <h2>🚪 Simulador de Garita / Torniquete Táctil</h2>
                    <p>Terminal de acceso restringido a <strong>room_911</strong></p>
                </div>

                <div className="garita-body">
                    <form onSubmit={handleEvaluar} className="garita-form">
                        <div className="input-group">
                            <label>ID Interno de Expediente:</label>
                            <input 
                                type="text" 
                                value={idUsuario} 
                                onChange={(e) => setIdUsuario(e.target.value.toUpperCase())}
                                placeholder="ej. EMP-8821"
                                className="garita-input"
                                required
                            />
                        </div>

                        <div className="tipo-evento-selector">
                            <button 
                                type="button"
                                className={`btn-toggle ${tipoEvento === 'ENTRADA' ? 'active-entrada' : ''}`}
                                onClick={() => setTipoEvento('ENTRADA')}
                            >
                                📥 ENTRADA
                            </button>
                            <button 
                                type="button"
                                className={`btn-toggle ${tipoEvento === 'SALIDA' ? 'active-salida' : ''}`}
                                onClick={() => setTipoEvento('SALIDA')}
                            >
                                📤 SALIDA
                            </button>
                        </div>

                        {/* Teclado Táctil Virtual */}
                        <div className="teclado-virtual">
                            {['1','2','3','4','5','6','7','8','9','EMP-','0','BACK'].map(btn => (
                                <button 
                                    key={btn} 
                                    type="button" 
                                    className="btn-key"
                                    onClick={() => handleTeclado(btn)}
                                >
                                    {btn}
                                </button>
                            ))}
                        </div>

                        <button type="submit" className="btn-evaluar" disabled={cargando}>
                            {cargando ? 'Evaluando ABAC...' : `SIMULAR ${tipoEvento}`}
                        </button>
                    </form>

                    {error && <div className="alert-error">{error}</div>}

                    {/* Pantalla de Resultado Táctil */}
                    {resultado && (
                        <div className={`pantalla-resultado ${resultado.permitido ? 'permitido' : 'denegado'}`}>
                            <div className="badge-resultado">
                                {resultado.permitido ? '✅ PERMITIDO' : '⛔ DENEGADO'}
                            </div>

                            <h3>{resultado.mensaje}</h3>

                            {resultado.motivoRechazo && (
                                <p className="motivo-rechazo">
                                    <strong>Motivo de Rechazo:</strong> {resultado.motivoRechazo}
                                </p>
                            )}

                            {/* Plan de Contingencia / Tarea Alternativa Automática */}
                            {resultado.tareaAlternativa && (
                                <div className="caja-contingencia">
                                    <h4>🔄 PLAN DE CONTINGENCIA (Redirección Automática)</h4>
                                    <p className="tarea-asignada">{resultado.tareaAlternativa}</p>
                                </div>
                            )}

                            <div className="resultado-footer">
                                <span>Expediente: <strong>{resultado.idUsuario}</strong></span> | 
                                <span> Timestamp: {new Date(resultado.timestamp).toLocaleTimeString()}</span>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SimuladorGarita;
