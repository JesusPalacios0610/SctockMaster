package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Integer> {

    // Buscar proveedor por RUC
    Optional<Proveedor> findByRuc(String ruc);

    // Comprobar si ya existe un proveedor con ese RUC
    boolean existsByRuc(String ruc);
}
