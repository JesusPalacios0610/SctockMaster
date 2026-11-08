package com.proyecto.electromarket.dto;

import jakarta.validation.constraints.NotNull;

public class EstadoRequest {

    @NotNull(message = "El estado activo es obligatorio")
    private Boolean activo;

    // ID del usuario que realiza el cambio de estado (opcional para auditoría)
    private Integer idUsuario;

    public EstadoRequest() {
    }

    public EstadoRequest(Boolean activo, Integer idUsuario) {
        this.activo = activo;
        this.idUsuario = idUsuario;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }
}
