package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.Producto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    // Consulta para Alertas ordenadas de menor a mayor stock
    @Query("SELECT p FROM Producto p WHERE p.stockActual <= p.stockMinimo ORDER BY p.stockActual ASC")
    List<Producto> findProductosAlertaStock();

    // Buscar producto con bloqueo pesimista de escritura (SELECT ... FOR UPDATE) para evitar condiciones de carrera en el stock
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.idProducto = :id")
    Optional<Producto> findByIdConBloqueo(@Param("id") Integer id);
}
