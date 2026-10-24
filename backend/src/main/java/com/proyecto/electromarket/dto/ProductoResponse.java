package com.proyecto.electromarket.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductoResponse {

    private Integer idProducto;
    private String codigoSku;
    private String nombre;
    private String categoria;
    private String marca;
    private BigDecimal precio;
    private String ubicacion;
    private Integer stockMinimo;
    private Integer stockActual;
    // Campo calculado que le avisa al frontend si este producto está en stock crítico
    private boolean alertaStock;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;

    // Constructores
    public ProductoResponse() {
    }

    public ProductoResponse(Integer idProducto, String codigoSku, String nombre,
                            String categoria, String marca, BigDecimal precio,
                            String ubicacion, Integer stockMinimo, Integer stockActual,
                            boolean alertaStock, LocalDateTime fechaCreacion,
                            LocalDateTime fechaActualizacion) {
        this.idProducto = idProducto;
        this.codigoSku = codigoSku;
        this.nombre = nombre;
        this.categoria = categoria;
        this.marca = marca;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.stockMinimo = stockMinimo;
        this.stockActual = stockActual;
        this.alertaStock = alertaStock;
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
    }

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoSku() {
        return codigoSku;
    }

    public void setCodigoSku(String codigoSku) {
        this.codigoSku = codigoSku;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public boolean isAlertaStock() {
        return alertaStock;
    }

    public void setAlertaStock(boolean alertaStock) {
        this.alertaStock = alertaStock;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
