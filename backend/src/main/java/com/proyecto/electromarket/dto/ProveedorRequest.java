package com.proyecto.electromarket.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProveedorRequest {

    @NotBlank(message = "El RUC es obligatorio")
    @Pattern(regexp = "\\d{11}", message = "El RUC debe tener exactamente 11 dígitos numéricos")
    private String ruc;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 150, message = "La razón social no puede superar los 150 caracteres")
    private String razonSocial;

    @Size(max = 100, message = "El contacto no puede superar los 100 caracteres")
    private String contacto;

    @Min(value = 0, message = "El tiempo de entrega no puede ser negativo")
    private Integer tiempoEntrega;

    // ID del usuario que registra o modifica el proveedor (para auditoría)
    private Integer idUsuario;

    // Constructores
    public ProveedorRequest() {
    }

    public ProveedorRequest(String ruc, String razonSocial, String contacto, Integer tiempoEntrega, Integer idUsuario) {
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.contacto = contacto;
        this.tiempoEntrega = tiempoEntrega;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters
    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public Integer getTiempoEntrega() {
        return tiempoEntrega;
    }

    public void setTiempoEntrega(Integer tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}
