package com.proyecto.electromarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "proveedor")
public class Proveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor")
    private Integer idProveedor;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "ruc", nullable = false, unique = true, length = 11)
    private String ruc;

    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    @Column(name = "contacto", length = 100)
    private String contacto;

    @Column(name = "telefono", length = 15)
    private String telefono;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "tiempo_entrega")
    private Integer tiempoEntrega;

    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    // Llenado por default en la BD al hacer INSERT
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Llenado por trigger en la BD al hacer UPDATE
    @Column(name = "fecha_actualizacion", insertable = false, updatable = false)
    private LocalDateTime fechaActualizacion;

    // Enviado por el backend con el id del usuario que realiza la modificación
    @Column(name = "usuario_actualizacion")
    private Integer usuarioActualizacion;

    // Constructor vacío (requerido por JPA)
    public Proveedor() {
    }

    // Constructor para facilitar la creación de instancias
    public Proveedor(String ruc, String razonSocial, String contacto, String telefono, String correo, Integer tiempoEntrega) {
        this.ruc = ruc;
        this.razonSocial = razonSocial;
        this.contacto = contacto;
        this.telefono = telefono;
        this.correo = correo;
        this.tiempoEntrega = tiempoEntrega;
        this.activo = true;
    }

    // Getters y Setters
    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getRuc() {
        return ruc != null ? ruc.trim() : null;
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

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Integer getTiempoEntrega() {
        return tiempoEntrega;
    }

    public void setTiempoEntrega(Integer tiempoEntrega) {
        this.tiempoEntrega = tiempoEntrega;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
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

    public Integer getUsuarioActualizacion() {
        return usuarioActualizacion;
    }

    public void setUsuarioActualizacion(Integer usuarioActualizacion) {
        this.usuarioActualizacion = usuarioActualizacion;
    }
}
