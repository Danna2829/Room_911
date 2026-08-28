import React, { useState, useEffect, useCallback } from 'react';
import api from '../api/api';

const PanelInventario = () => {
    const [movimientos, setMovimientos] = useState([]);
    const [categorias, setCategorias] = useState([]);
    const [idCategoria, setIdCategoria] = useState('');
    const [cantidad, setCantidad] = useState(10);
    const [tipoMovimiento, setTipoMovimiento] = useState('ENTRADA');
    const [lote, setLote] = useState('LOTE-2026-A');
    const [observaciones, setObservaciones] = useState('');
    const [mensaje, setMensaje] = useState('');
    const [cargando, setCargando] = useState(false);

    // Nueva categoría
    const [codigoCat, setCodigoCat] = useState('');
    const [nombreCat, setNombreCat] = useState('');
    const [descCat, setDescCat] = useState('');
    const [restringidoCat, setRestringidoCat] = useState(false);

    const cargarDatos = useCallback(async () => {
        try {
            const [resInv, resCat] = await Promise.all([
                api.get('/inventario'),
                api.get('/categorias')
            ]);
            setMovimientos(resInv.data);
            setCategorias(resCat.data);
            if (resCat.data.length > 0 && !idCategoria) {
                setIdCategoria(resCat.data[0].id);
            }
        } catch (err) {
            console.error('Error cargando datos de inventario:', err);
        }
    }, [idCategoria]);

    useEffect(() => {
        cargarDatos();
    }, [cargarDatos]);

    const handleRegistrarMovimiento = async (e) => {
        e.preventDefault();
        setCargando(true);
        setMensaje('');
        try {
            const endpoint = tipoMovimiento === 'ENTRADA' ? '/inventario/entrada' : '/inventario/salida';
            await api.post(endpoint, {
                idCategoria: parseInt(idCategoria),
                cantidad: parseInt(cantidad),
                lote,
                observaciones
            });
            setMensaje('✅ Movimiento de inventario registrado con éxito.');
            setObservaciones('');
            cargarDatos();
        } catch (err) {
            console.error('Error guardando movimiento:', err);
            setMensaje('❌ Error al registrar movimiento de inventario.');
        } finally {
            setCargando(false);
        }
    };

    const handleCrearCategoria = async (e) => {
        e.preventDefault();
        if (!codigoCat.trim() || !nombreCat.trim()) return;
        try {
            await api.post('/categorias', {
                codigo: codigoCat.trim().toUpperCase(),
                nombre: nombreCat.trim(),
                descripcion: descCat.trim(),
                esRestringido: restringidoCat
            });
            setCodigoCat('');
            setNombreCat('');
            setDescCat('');
            cargarDatos();
            alert('Categoría creada exitosamente.');
        } catch (err) {
            console.error('Error creando categoría:', err);
            alert('Error al crear categoría.');
        }
    };

    return (
        <div className="module-container">
            <div className="module-card">
                <h2>💊 Inventario y Gestión de Categorías de Medicamento</h2>
                <p>Control de existencias físicas, lotes y niveles de riesgo farmacéutico en room_911</p>

                <div className="grid-dos-columnas">
                    {/* Formulario Movimiento Inventario */}
                    <div className="seccion-formulario">
                        <h3>📦 Registrar Movimiento de Stock</h3>
                        <form onSubmit={handleRegistrarMovimiento} className="form-vertical">
                            <div className="form-group">
                                <label>Categoría de Sustancia:</label>
                                <select value={idCategoria} onChange={(e) => setIdCategoria(e.target.value)} required>
                                    {categorias.map(c => (
                                        <option key={c.id} value={c.id}>
                                            {c.nombre} ({c.codigo}) {c.esRestringido ? '⚠️ RESTRINGIDO' : ''}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div className="form-group">
                                <label>Tipo de Movimiento:</label>
                                <select value={tipoMovimiento} onChange={(e) => setTipoMovimiento(e.target.value)}>
                                    <option value="ENTRADA">📥 ENTRADA (Ingreso a sala)</option>
                                    <option value="SALIDA">📤 SALIDA (Uso / Despacho)</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label>Cantidad de Unidades:</label>
                                <input 
                                    type="number" 
                                    min="1"
                                    value={cantidad} 
                                    onChange={(e) => setCantidad(e.target.value)} 
                                    required 
                                />
                            </div>

                            <div className="form-group">
                                <label>Número de Lote:</label>
                                <input 
                                    type="text" 
                                    value={lote} 
                                    onChange={(e) => setLote(e.target.value)} 
                                    placeholder="ej. LOTE-2026-X" 
                                    required 
                                />
                            </div>

                            <div className="form-group">
                                <label>Observaciones:</label>
                                <textarea 
                                    value={observaciones} 
                                    onChange={(e) => setObservaciones(e.target.value)} 
                                    placeholder="Detalles del lote, proveedor o formulación..."
                                    rows="2"
                                />
                            </div>

                            <button type="submit" className="btn-primary" disabled={cargando}>
                                {cargando ? 'Guardando...' : `Registrar ${tipoMovimiento}`}
                            </button>
                        </form>

                        {mensaje && <div className="info-mensaje">{mensaje}</div>}

                        {/* Crear Categoría Rápida */}
                        <div style={{ marginTop: '30px', padding: '15px', background: '#f8fafc', borderRadius: '8px', border: '1px dashed #cbd5e1' }}>
                            <h4>➕ Registrar Nueva Categoría de Riesgo</h4>
                            <form onSubmit={handleCrearCategoria} className="form-vertical" style={{ marginTop: '10px' }}>
                                <input 
                                    type="text" 
                                    placeholder="Código (ej. TIPO_6)" 
                                    value={codigoCat} 
                                    onChange={(e) => setCodigoCat(e.target.value)} 
                                    required 
                                    style={{ padding: '8px', marginBottom: '8px' }}
                                />
                                <input 
                                    type="text" 
                                    placeholder="Nombre de la categoría" 
                                    value={nombreCat} 
                                    onChange={(e) => setNombreCat(e.target.value)} 
                                    required 
                                    style={{ padding: '8px', marginBottom: '8px' }}
                                />
                                <label style={{ fontSize: '13px', display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px' }}>
                                    <input 
                                        type="checkbox" 
                                        checked={restringidoCat} 
                                        onChange={(e) => setRestringidoCat(e.target.checked)} 
                                    />
                                    ¿Es sustancia de Alto Riesgo / Restringida?
                                </label>
                                <button type="submit" className="btn-secondary" style={{ padding: '6px 12px' }}>
                                    Guardar Categoría
                                </button>
                            </form>
                        </div>
                    </div>

                    {/* Tabla de Movimientos */}
                    <div className="seccion-tabla">
                        <h3>📋 Historial de Movimientos de Inventario</h3>
                        <div className="tabla-scroll" style={{ maxHeight: '550px', overflowY: 'auto' }}>
                            <table className="tabla-estandar">
                                <thead>
                                    <tr>
                                        <th>Fecha / Hora</th>
                                        <th>Tipo</th>
                                        <th>Categoría</th>
                                        <th>Cantidad</th>
                                        <th>Lote</th>
                                        <th>Observaciones</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {movimientos.length === 0 ? (
                                        <tr><td colSpan="6">No hay movimientos registrados en inventario</td></tr>
                                    ) : (
                                        movimientos.map(m => {
                                            const cat = categorias.find(c => c.id === m.idCategoria);
                                            return (
                                                <tr key={m.id}>
                                                    <td style={{ fontSize: '12px' }}>
                                                        {m.timestamp ? new Date(m.timestamp).toLocaleString() : '-'}
                                                    </td>
                                                    <td>
                                                        <span className={`tag-${m.tipoMovimiento === 'ENTRADA' ? 'activo' : 'inactivo'}`}>
                                                            {m.tipoMovimiento}
                                                        </span>
                                                    </td>
                                                    <td><strong>{cat ? cat.nombre : `Cat ID ${m.idCategoria}`}</strong></td>
                                                    <td><strong>{m.cantidad} uds</strong></td>
                                                    <td><code>{m.lote}</code></td>
                                                    <td style={{ fontSize: '12px', color: '#64748b' }}>{m.observaciones || '-'}</td>
                                                </tr>
                                            );
                                        })
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

export default PanelInventario;
