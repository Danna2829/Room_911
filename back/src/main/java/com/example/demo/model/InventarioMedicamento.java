package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventario_medicamentos")
public class InventarioMedicamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_categoria", nullable = false)
    private Long idCategoria; // ID de la categoría de medicamento

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "tipo_movimiento", nullable = false)
    private String tipoMovimiento; // ENTRADA, SALIDA, AJUSTE

    @Column(name = "lote", nullable = false)
    private String lote;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "activo")
    private Boolean activo = true;

    public InventarioMedicamento() {
        this.timestamp = LocalDateTime.now();
        this.activo = true;
    }

    public InventarioMedicamento(Long idCategoria, Integer cantidad, String tipoMovimiento, String lote, String observaciones) {
        this.idCategoria = idCategoria;
        this.cantidad = cantidad;
        this.tipoMovimiento = tipoMovimiento;
        this.lote = lote;
        this.observaciones = observaciones;
        this.timestamp = LocalDateTime.now();
        this.activo = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdCategoria() { return idCategoria; }
    public void setIdCategoria(Long idCategoria) { this.idCategoria = idCategoria; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public String getTipoMovimiento() { return tipoMovimiento; }
    public void setTipoMovimiento(String tipoMovimiento) { this.tipoMovimiento = tipoMovimiento; }

    public String getLote() { return lote; }
    public void setLote(String lote) { this.lote = lote; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Boolean getActivo() { return activo != null ? activo : true; }
    public void setActivo(Boolean activo) { this.activo = activo != null ? activo : true; }
}
