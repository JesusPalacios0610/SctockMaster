package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {

    // Buscar producto por su código SKU
    Optional<Producto> findByCodigoSku(String codigoSku);

    // Comprobar si ya existe un producto con ese código SKU
    boolean existsByCodigoSku(String codigoSku);

    // Búsqueda por nombre o por SKU ignorando mayúsculas/minúsculas (para el buscador del frontend)
    List<Producto> findByNombreContainingIgnoreCaseOrCodigoSkuContainingIgnoreCase(String nombre, String codigoSku);

    // Alerta de stock mínimo: productos donde stock_actual <= stock_minimo
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo")
    List<Producto> findProductosConBajoStock();
}
