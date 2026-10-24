package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.SalidaStock;
import org.springframework.data.jpa.repository.JpaRepository;
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
}
