package com.proyecto.electromarket.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "producto")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Integer idProducto;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "codigo_sku", nullable = false, unique = true, length = 20)
    private String codigoSku;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @Column(name = "marca", length = 50)
    private String marca;

    @Column(name = "precio", nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    @Column(name = "ubicacion", length = 50)
    private String ubicacion;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo = 0;

    @Column(name = "stock_actual", nullable = false)
    private Integer stockActual = 0;

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
    public Producto() {
    }

    // Constructor para inicializar los datos principales de un producto
    public Producto(String codigoSku, String nombre, String categoria, String marca,
                    BigDecimal precio, String ubicacion, Integer stockMinimo, Integer stockActual) {
        this.codigoSku = codigoSku;
        this.nombre = nombre;
        this.categoria = categoria;
        this.marca = marca;
        this.precio = precio;
        this.ubicacion = ubicacion;
        this.stockMinimo = stockMinimo != null ? stockMinimo : 0;
        this.stockActual = stockActual != null ? stockActual : 0;
    }

    // Getters y Setters
    public Integer getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Integer idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodigoSku() {
        // En PostgreSQL CHAR(20) rellena con espacios a la derecha; trim() los limpia
        return codigoSku != null ? codigoSku.trim() : null;
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

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Integer getStockMinimo() {
        return stockMinimo;
    }

    public void setStockMinimo(Integer stockMinimo) {
        this.stockMinimo = stockMinimo;
    }

    public Integer getStockActual() {
        return stockActual;
    }

    public void setStockActual(Integer stockActual) {
        this.stockActual = stockActual;
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
