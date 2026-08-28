import React, { useState, useEffect, useCallback } from 'react';
import api from '../api/api';

const PanelSecretaria = () => {
    const [fecha, setFecha] = useState(new Date().toISOString().split('T')[0]);
    const [idCategoria, setIdCategoria] = useState(4); // Default Tipo 4
    const [observaciones, setObservaciones] = useState('Procesamiento programado por Secretaría');
    const [categorias, setCategorias] = useState([]);
    const [cronogramaHoy, setCronogramaHoy] = useState(null);
    const [mensaje, setMensaje] = useState('');
    const [cargando, setCargando] = useState(false);

    const cargarCategorias = useCallback(async () => {
        try {
            const res = await api.get('/categorias');
            setCategorias(res.data);
        } catch (err) {
            console.error('Error cargando categorías:', err);
        }
    }, []);

    const cargarCronogramaHoy = useCallback(async () => {
        try {
            const res = await api.get('/cronograma/hoy');
            setCronogramaHoy(res.data);
        } catch (err) {
            console.log('No hay cronograma programado para hoy todavía');
            setCronogramaHoy(null);
        }
    }, []);

    useEffect(() => {
        cargarCategorias();
        cargarCronogramaHoy();
    }, [cargarCategorias, cargarCronogramaHoy]);

    const handleGuardar = async (e) => {
        e.preventDefault();
        setCargando(true);
        setMensaje('');

        try {
            await api.post('/cronograma', {
                fecha,
                idCategoria: parseInt(idCategoria),
                observaciones
            });
            setMensaje('✅ Cronograma diario guardado correctamente');
            cargarCronogramaHoy();
        } catch (err) {
            console.error('Error al guardar cronograma:', err);
            setMensaje('❌ Error al guardar el cronograma');
        } finally {
            setCargando(false);
        }
    };

    const getNombreCategoria = (id) => {
        const cat = categorias.find(c => c.id === id);
        return cat ? `${cat.nombre} (${cat.codigo})` : `Categoría ID ${id}`;
    };

    return (
        <div className="module-container">
            <div className="module-card">
                <h2>📅 Panel de Secretaría - Cronograma Operativo Diario</h2>
                <p>Planificación de categorías de medicamentos manipuladas en <strong>room_911</strong></p>

                {cronogramaHoy ? (
                    <div className="badge-cronograma-hoy">
                        📌 <strong>Actividad Programada Hoy:</strong> {getNombreCategoria(cronogramaHoy.idCategoria)}
                        {cronogramaHoy.idCategoria === 4 && <span className="tag-restringido"> 🔥 ALTO CONTROL (RESTRINGIDO)</span>}
                    </div>
                ) : (
                    <div className="badge-cronograma-vacio">
                        ⚠️ No se ha programado actividad específica para el día de hoy.
                    </div>
                )}

                <form onSubmit={handleGuardar} className="form-grid">
                    <div className="form-group">
                        <label>Fecha de Operación:</label>
                        <input 
                            type="date" 
                            value={fecha} 
                            onChange={(e) => setFecha(e.target.value)}
                            required
                        />
                    </div>

                    <div className="form-group">
                        <label>Categoría / Tipo de Medicamento:</label>
                        <select 
                            value={idCategoria} 
                            onChange={(e) => setIdCategoria(e.target.value)}
                            required
                        >
                            {categorias.map(cat => (
                                <option key={cat.id} value={cat.id}>
                                    {cat.nombre} {cat.esRestringido ? '⚠️ (Restringido)' : ''}
                                </option>
                            ))}
                        </select>
                    </div>

                    <div className="form-group full-width">
                        <label>Observaciones / Lote en Producción:</label>
                        <textarea 
                            value={observaciones} 
                            onChange={(e) => setObservaciones(e.target.value)}
                            rows="2"
                        />
                    </div>

                    <button type="submit" className="btn-primary" disabled={cargando}>
                        {cargando ? 'Guardando...' : 'Programar Categoría en Cronograma'}
                    </button>
                </form>

                {mensaje && <div className="info-mensaje">{mensaje}</div>}
            </div>
        </div>
    );
};

export default PanelSecretaria;
