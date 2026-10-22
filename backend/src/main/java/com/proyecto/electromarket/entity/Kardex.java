package com.proyecto.electromarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "kardex")
public class Kardex {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kardex")
    private Integer idKardex;

    // Relación ManyToOne con Producto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    @Column(name = "fecha_calculo", nullable = false)
    private LocalDate fechaCalculo;

    @Column(name = "stock_inicial", nullable = false)
    private Integer stockInicial;

    @Column(name = "entradas", nullable = false)
    private Integer entradas = 0;

    @Column(name = "salidas", nullable = false)
    private Integer salidas = 0;

    @Column(name = "stock_final", nullable = false)
    private Integer stockFinal;

    // Llenado por default en la BD al hacer INSERT
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Constructor vacío (requerido por JPA)
    public Kardex() {
    }

    // Constructor para registrar un movimiento en Kardex
    public Kardex(Producto producto, LocalDate fechaCalculo, Integer stockInicial,
                  Integer entradas, Integer salidas, Integer stockFinal) {
        this.producto = producto;
        this.fechaCalculo = fechaCalculo != null ? fechaCalculo : LocalDate.now();
        this.stockInicial = stockInicial;
        this.entradas = entradas != null ? entradas : 0;
        this.salidas = salidas != null ? salidas : 0;
        this.stockFinal = stockFinal;
    }

    // Getters y Setters
    public Integer getIdKardex() {
        return idKardex;
    }

    public void setIdKardex(Integer idKardex) {
        this.idKardex = idKardex;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public LocalDate getFechaCalculo() {
        return fechaCalculo;
    }

    public void setFechaCalculo(LocalDate fechaCalculo) {
        this.fechaCalculo = fechaCalculo;
    }

    public Integer getStockInicial() {
        return stockInicial;
    }

    public void setStockInicial(Integer stockInicial) {
        this.stockInicial = stockInicial;
    }

    public Integer getEntradas() {
        return entradas;
    }

    public void setEntradas(Integer entradas) {
        this.entradas = entradas;
    }

    public Integer getSalidas() {
        return salidas;
    }

    public void setSalidas(Integer salidas) {
        this.salidas = salidas;
    }

    public Integer getStockFinal() {
        return stockFinal;
    }

    public void setStockFinal(Integer stockFinal) {
        this.stockFinal = stockFinal;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
