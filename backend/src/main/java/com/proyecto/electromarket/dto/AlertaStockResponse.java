package com.proyecto.electromarket.dto;

public class AlertaStockResponse {

    private Integer idProducto;
    private String codigoSku;
    private String nombre;
    private Integer stockActual;
    private Integer stockMinimo;
    private Integer diferencia; // stockMinimo - stockActual (cuántas unidades faltan para alcanzar el mínimo)

    // Constructores
    public AlertaStockResponse() {
    }

    public AlertaStockResponse(Integer idProducto, String codigoSku, String nombre,
                               Integer stockActual, Integer stockMinimo, Integer diferencia) {
        this.idProducto = idProducto;
        this.codigoSku = codigoSku;
        this.nombre = nombre;
        this.stockActual = stockActual;
        this.stockMinimo = stockMinimo;
        this.diferencia = diferencia;
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

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(Integer diferencia) {
        this.diferencia = diferencia;
    }
}
