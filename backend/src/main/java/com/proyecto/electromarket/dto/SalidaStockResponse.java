package com.proyecto.electromarket.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class SalidaStockResponse {

    private Integer idSalida;
    private Integer idProducto;
    private String codigoSku;
    private String nombreProducto;
    private Integer idUsuario;
    private String nombreUsuario; // Seguro: sin contraseña
    private String documento;
    private LocalDate fecha;
    private String tipoSalida;
    private Integer cantidad;
    private Integer stockRestante; // Muy útil para que el frontend actualice el inventario de inmediato
    private LocalDateTime fechaCreacion;

    // Constructores
    public SalidaStockResponse() {
    }

    public SalidaStockResponse(Integer idSalida, Integer idProducto, String codigoSku,
                               String nombreProducto, Integer idUsuario, String nombreUsuario,
                               String documento, LocalDate fecha, String tipoSalida,
                               Integer cantidad, Integer stockRestante, LocalDateTime fechaCreacion) {
        this.idSalida = idSalida;
        this.idProducto = idProducto;
        this.codigoSku = codigoSku;
        this.nombreProducto = nombreProducto;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.documento = documento;
        this.fecha = fecha;
        this.tipoSalida = tipoSalida;
        this.cantidad = cantidad;
        this.stockRestante = stockRestante;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public Integer getIdSalida() {
        return idSalida;
    }

    public void setIdSalida(Integer idSalida) {
        this.idSalida = idSalida;
    }

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

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipoSalida() {
        return tipoSalida;
    }

    public void setTipoSalida(String tipoSalida) {
        this.tipoSalida = tipoSalida;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getStockRestante() {
        return stockRestante;
    }

    public void setStockRestante(Integer stockRestante) {
        this.stockRestante = stockRestante;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
