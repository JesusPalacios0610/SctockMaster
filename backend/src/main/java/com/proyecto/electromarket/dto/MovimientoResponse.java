package com.proyecto.electromarket.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MovimientoResponse {

    private String id; // Texto único: ej. "ENT-12" o "SAL-5"
    private LocalDate fecha;
    private String tipo; // "Entrada" o "Salida"
    private String documento;
    private String motivo; // Para Entrada: FACTURA, GUIA_REMISION, etc. Para Salida: VENTA, TRASLADO, MERMA
    private Integer idProducto;
    private String codigoSku;
    private String nombreProducto;
    private Integer cantidad;
    private Integer saldo; // Stock del producto después del movimiento
    private String usuario; // Nombre del usuario que lo registró
    private LocalDateTime fechaCreacion;

    public MovimientoResponse() {
    }

    public MovimientoResponse(String id, LocalDate fecha, String tipo, String documento,
                              String motivo, Integer idProducto, String codigoSku,
                              String nombreProducto, Integer cantidad, Integer saldo,
                              String usuario, LocalDateTime fechaCreacion) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.documento = documento;
        this.motivo = motivo;
        this.idProducto = idProducto;
        this.codigoSku = codigoSku;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.saldo = saldo;
        this.usuario = usuario;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
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

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
