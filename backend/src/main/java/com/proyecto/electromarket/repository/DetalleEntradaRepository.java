package com.proyecto.electromarket.repository;

import com.proyecto.electromarket.entity.DetalleEntrada;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DetalleEntradaRepository extends JpaRepository<DetalleEntrada, Integer> {

    // Listar todos los detalles (productos) que pertenecen a una entrada
    List<DetalleEntrada> findByEntradaIdEntrada(Integer idEntrada);
}
