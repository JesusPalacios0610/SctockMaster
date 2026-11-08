package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.SalidaStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface SalidaStockRepository extends JpaRepository<SalidaStock, Integer> {

    // Listar salidas en un rango de fechas, ordenadas de más reciente a más antigua
    List<SalidaStock> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    // Listar todas las salidas de un producto específico
    List<SalidaStock> findByProductoIdProductoOrderByFechaDesc(Integer idProducto);

    // Listar todas ordenadas por fecha descendente
    List<SalidaStock> findAllByOrderByFechaDesc();

    // Sumar salidas de un producto en una fecha dada usando COALESCE para devolver 0 si no hay registros
    @Query("SELECT COALESCE(SUM(s.cantidad), 0) FROM SalidaStock s WHERE s.producto.idProducto = :idProducto AND s.fecha = :fecha")
    Integer sumarSalidasPorProductoYFecha(@Param("idProducto") Integer idProducto, @Param("fecha") LocalDate fecha);

    // Traer todas las salidas de un producto con JOIN FETCH para evitar N+1
    @Query("SELECT s FROM SalidaStock s " +
           "JOIN FETCH s.producto p " +
           "JOIN FETCH s.usuario u " +
           "WHERE p.idProducto = :idProducto")
    List<SalidaStock> findByProductoIdConRelaciones(@Param("idProducto") Integer idProducto);

    // Traer todas las salidas de todos los productos con JOIN FETCH para evitar N+1
    @Query("SELECT s FROM SalidaStock s " +
           "JOIN FETCH s.producto p " +
           "JOIN FETCH s.usuario u")
    List<SalidaStock> findAllConRelaciones();
}
