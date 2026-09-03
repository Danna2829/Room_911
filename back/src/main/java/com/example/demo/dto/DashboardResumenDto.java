package com.example.demo.dto;

import com.example.demo.model.RegistroAuditoria;

import java.util.List;

/**
 * Metricas operativas del Panel General (datos reales del sistema).
 */
public class DashboardResumenDto {

    private long accesosHoy;
    private long permitidosHoy;
    private long denegadosHoy;
    private long operariosActivos;
    private long medicamentosActivos;
    private long programadosHoy;
    private long suspensionesVigentes;
    private List<RegistroAuditoria> ultimosAccesos;

    public long getAccesosHoy() { return accesosHoy; }
    public void setAccesosHoy(long accesosHoy) { this.accesosHoy = accesosHoy; }

    public long getPermitidosHoy() { return permitidosHoy; }
    public void setPermitidosHoy(long permitidosHoy) { this.permitidosHoy = permitidosHoy; }

    public long getDenegadosHoy() { return denegadosHoy; }
    public void setDenegadosHoy(long denegadosHoy) { this.denegadosHoy = denegadosHoy; }

    public long getOperariosActivos() { return operariosActivos; }
    public void setOperariosActivos(long operariosActivos) { this.operariosActivos = operariosActivos; }

    public long getMedicamentosActivos() { return medicamentosActivos; }
    public void setMedicamentosActivos(long medicamentosActivos) { this.medicamentosActivos = medicamentosActivos; }

    public long getProgramadosHoy() { return programadosHoy; }
    public void setProgramadosHoy(long programadosHoy) { this.programadosHoy = programadosHoy; }

    public long getSuspensionesVigentes() { return suspensionesVigentes; }
    public void setSuspensionesVigentes(long suspensionesVigentes) { this.suspensionesVigentes = suspensionesVigentes; }

    public List<RegistroAuditoria> getUltimosAccesos() { return ultimosAccesos; }
    public void setUltimosAccesos(List<RegistroAuditoria> ultimosAccesos) { this.ultimosAccesos = ultimosAccesos; }
}
