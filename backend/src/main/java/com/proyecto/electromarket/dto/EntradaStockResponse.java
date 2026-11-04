package com.proyecto.electromarket.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class EntradaStockResponse {

    private Integer idEntrada;
    private Integer idProveedor;
    private String razonSocialProveedor;
    private Integer idUsuario;
    private String nombreUsuario; // Seguro: solo nombre, sin contraseña
    private String documento;
    private LocalDate fecha;
    private String estado;
    private LocalDateTime fechaCreacion;
    private List<DetalleEntradaResponse> detalles;

    // Constructores
    public EntradaStockResponse() {
    }

    public EntradaStockResponse(Integer idEntrada, Integer idProveedor, String razonSocialProveedor,
                                Integer idUsuario, String nombreUsuario, String documento,
                                LocalDate fecha, String estado, LocalDateTime fechaCreacion,
                                List<DetalleEntradaResponse> detalles) {
        this.idEntrada = idEntrada;
        this.idProveedor = idProveedor;
        this.razonSocialProveedor = razonSocialProveedor;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.documento = documento;
        this.fecha = fecha;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.detalles = detalles;
    }

    // Getters y Setters
    public Integer getIdEntrada() {
        return idEntrada;
    }

    public void setIdEntrada(Integer idEntrada) {
        this.idEntrada = idEntrada;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getRazonSocialProveedor() {
        return razonSocialProveedor;
    }

    public void setRazonSocialProveedor(String razonSocialProveedor) {
        this.razonSocialProveedor = razonSocialProveedor;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public List<DetalleEntradaResponse> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleEntradaResponse> detalles) {
        this.detalles = detalles;
    }
}
