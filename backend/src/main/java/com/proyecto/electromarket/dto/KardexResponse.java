package com.proyecto.electromarket.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class KardexResponse {

    private Integer idKardex;
    private Integer idProducto;
    private String codigoSku;
    private String nombreProducto;
    private LocalDate fechaCalculo;
    private Integer stockInicial;
    private Integer entradas;
    private Integer salidas;
    private Integer stockFinal;
    private LocalDateTime fechaCreacion;

    // Constructores
    public KardexResponse() {
    }

    public KardexResponse(Integer idKardex, Integer idProducto, String codigoSku,
                          String nombreProducto, LocalDate fechaCalculo,
                          Integer stockInicial, Integer entradas, Integer salidas,
                          Integer stockFinal, LocalDateTime fechaCreacion) {
        this.idKardex = idKardex;
        this.idProducto = idProducto;
        this.codigoSku = codigoSku;
        this.nombreProducto = nombreProducto;
        this.fechaCalculo = fechaCalculo;
        this.stockInicial = stockInicial;
        this.entradas = entradas;
        this.salidas = salidas;
        this.stockFinal = stockFinal;
        this.fechaCreacion = fechaCreacion;
    }

    // Getters y Setters
    public Integer getIdKardex() {
        return idKardex;
    }

    public void setIdKardex(Integer idKardex) {
        this.idKardex = idKardex;
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
