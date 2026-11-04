package com.proyecto.electromarket.dto;

public class DetalleEntradaResponse {

    private Integer idDetalle;
    private Integer idProducto;
    private String codigoSku;
    private String nombreProducto;
    private Integer cantidad;

    // Constructores
    public DetalleEntradaResponse() {
    }

    public DetalleEntradaResponse(Integer idDetalle, Integer idProducto, String codigoSku,
                                  String nombreProducto, Integer cantidad) {
        this.idDetalle = idDetalle;
        this.idProducto = idProducto;
        this.codigoSku = codigoSku;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
    }

    // Getters y Setters
    public Integer getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(Integer idDetalle) {
        this.idDetalle = idDetalle;
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
}
