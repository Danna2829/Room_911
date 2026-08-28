import React, { useState } from 'react';
import SimuladorGarita from './components/SimuladorGarita';
import PanelSecretaria from './components/PanelSecretaria';
import PanelGuardia from './components/PanelGuardia';
import AdminUsuarios from './components/AdminUsuarios';
import PanelReportes from './components/PanelReportes';
import PanelInventario from './components/PanelInventario';
import './App.css';

function App() {
  const [tabActiva, setTabActiva] = useState('GARITA');

  return (
    <div className="app-container">
      {/* Header Principal */}
      <header className="app-header">
        <div className="header-brand">
          <h1>🔬 Sistema room_911</h1>
          <span>Control de Acceso Dinámico por Matriz de Riesgo y Cronograma (ABAC/RBAC)</span>
        </div>

        {/* Navegación por pestañas */}
        <nav className="nav-tabs">
          <button 
            className={`nav-item ${tabActiva === 'GARITA' ? 'active' : ''}`}
            onClick={() => setTabActiva('GARITA')}
          >
            🚪 Garita / Torniquete
          </button>
          <button 
            className={`nav-item ${tabActiva === 'SECRETARIA' ? 'active' : ''}`}
            onClick={() => setTabActiva('SECRETARIA')}
          >
            📅 Secretaría (Cronograma)
          </button>
          <button 
            className={`nav-item ${tabActiva === 'GUARDIA' ? 'active' : ''}`}
            onClick={() => setTabActiva('GUARDIA')}
          >
            🛡️ Guardia (Seguridad & Monitor)
          </button>
          <button 
            className={`nav-item ${tabActiva === 'ADMIN' ? 'active' : ''}`}
            onClick={() => setTabActiva('ADMIN')}
          >
            👤 Usuarios (Admin)
          </button>
          <button 
            className={`nav-item ${tabActiva === 'REPORTES' ? 'active' : ''}`}
            onClick={() => setTabActiva('REPORTES')}
          >
            📊 Auditoría & Reportes
          </button>
          <button 
            className={`nav-item ${tabActiva === 'INVENTARIO' ? 'active' : ''}`}
            onClick={() => setTabActiva('INVENTARIO')}
          >
            💊 Inventario & Categorías
          </button>
        </nav>
      </header>

      {/* Contenido Principal */}
      <main className="app-main">
        {tabActiva === 'GARITA' && <SimuladorGarita />}
        {tabActiva === 'SECRETARIA' && <PanelSecretaria />}
        {tabActiva === 'GUARDIA' && <PanelGuardia />}
        {tabActiva === 'ADMIN' && <AdminUsuarios />}
        {tabActiva === 'REPORTES' && <PanelReportes />}
        {tabActiva === 'INVENTARIO' && <PanelInventario />}
      </main>

      <footer className="app-footer">
        <p>Laboratorio Farmacéutico room_911 — Programa de Aceleración Técnica</p>
      </footer>
    </div>
  );
}

export default App;
