package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.EntradaStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EntradaStockRepository extends JpaRepository<EntradaStock, Integer> {

    // Buscar entrada por número de documento (factura o guía)
    Optional<EntradaStock> findByDocumento(String documento);

    // Listar entradas en un rango de fechas, ordenadas de más reciente a más antigua
    List<EntradaStock> findByFechaBetweenOrderByFechaDesc(LocalDate desde, LocalDate hasta);

    // Listar todas ordenadas por fecha descendente
    List<EntradaStock> findAllByOrderByFechaDesc();
}
