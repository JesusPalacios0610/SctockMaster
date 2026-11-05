package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.DetalleEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DetalleEntradaRepository extends JpaRepository<DetalleEntrada, Integer> {

    // Listar todos los detalles (productos) que pertenecen a una entrada
    List<DetalleEntrada> findByEntradaIdEntrada(Integer idEntrada);

    // Sumar entradas de un producto en una fecha dada usando COALESCE para devolver 0 si no hay registros
    @Query("SELECT COALESCE(SUM(d.cantidad), 0) FROM DetalleEntrada d WHERE d.producto.idProducto = :idProducto AND d.entrada.fecha = :fecha")
    Integer sumarEntradasPorProductoYFecha(@Param("idProducto") Integer idProducto, @Param("fecha") LocalDate fecha);
}
