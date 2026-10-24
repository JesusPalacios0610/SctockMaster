package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.Kardex;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface KardexRepository extends JpaRepository<Kardex, Integer> {

    // Obtener los registros de kardex de un producto ordenados por fecha de cálculo descendente
    List<Kardex> findByProductoIdProductoOrderByFechaCalculoDesc(Integer idProducto);

    // Obtener el registro de kardex de un producto en una fecha determinada
    Optional<Kardex> findByProductoIdProductoAndFechaCalculo(Integer idProducto, LocalDate fechaCalculo);
}
