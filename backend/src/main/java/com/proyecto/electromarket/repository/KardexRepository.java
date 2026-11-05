package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KardexRepository extends JpaRepository<Kardex, Integer> {

    // Obtener los registros de kardex de un producto ordenados por fecha de cálculo descendente
    List<Kardex> findByProductoIdProductoOrderByFechaCalculoDesc(Integer idProducto);

    // Obtener el registro de kardex de un producto en una fecha determinada (evitar duplicados al generar hoy)
    Optional<Kardex> findByProductoIdProductoAndFechaCalculo(Integer idProducto, LocalDate fechaCalculo);

    // Consultar kardex por producto y rango de fechas
    @Query("SELECT k FROM Kardex k WHERE k.producto.idProducto = :idProducto " +
           "AND (:desde IS NULL OR k.fechaCalculo >= :desde) " +
           "AND (:hasta IS NULL OR k.fechaCalculo <= :hasta) " +
           "ORDER BY k.fechaCalculo DESC")
    List<Kardex> filtrarPorProductoYFechas(@Param("idProducto") Integer idProducto,
                                          @Param("desde") LocalDate desde,
                                          @Param("hasta") LocalDate hasta);

    // Consultar kardex de TODOS los productos en un rango de fechas
    @Query("SELECT k FROM Kardex k WHERE (:desde IS NULL OR k.fechaCalculo >= :desde) " +
           "AND (:hasta IS NULL OR k.fechaCalculo <= :hasta) " +
           "ORDER BY k.fechaCalculo DESC, k.producto.nombre ASC")
    List<Kardex> filtrarSoloPorFechas(@Param("desde") LocalDate desde,
                                     @Param("hasta") LocalDate hasta);
}
