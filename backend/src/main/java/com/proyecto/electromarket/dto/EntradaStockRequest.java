package com.proyecto.electromarket.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;

public class EntradaStockRequest {

    @NotNull(message = "El ID del proveedor es obligatorio")
    private Integer idProveedor;

    @NotNull(message = "El ID del usuario es obligatorio")
    private Integer idUsuario;

    @NotBlank(message = "El tipo de documento es obligatorio")
    @Size(max = 20, message = "El tipo de documento no puede superar los 20 caracteres")
    private String tipoDocumento;

    @NotBlank(message = "El número de documento es obligatorio")
    @Size(max = 20, message = "El documento no puede superar los 20 caracteres")
    private String documento;

    // Opcional: si es null, el servicio asignará la fecha actual
    private LocalDate fecha;

    @NotEmpty(message = "La entrada debe contener al menos una línea de producto")
    @Valid
    private List<DetalleEntradaRequest> detalles;

    // Constructores
    public EntradaStockRequest() {
    }

    public EntradaStockRequest(Integer idProveedor, Integer idUsuario, String tipoDocumento,
                               String documento, LocalDate fecha, List<DetalleEntradaRequest> detalles) {
        this.idProveedor = idProveedor;
        this.idUsuario = idUsuario;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.fecha = fecha;
        this.detalles = detalles;
    }

    // Getters y Setters
    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
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

    public List<DetalleEntradaRequest> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleEntradaRequest> detalles) {
        this.detalles = detalles;
    }
}
