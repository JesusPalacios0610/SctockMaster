package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.EstadoRequest;
import com.proyecto.electromarket.dto.ProductoRequest;
import com.proyecto.electromarket.dto.ProductoResponse;
import java.util.List;

public interface ProductoService {

    // Obtener todos los productos (activos e inactivos)
    List<ProductoResponse> listarTodos();

    // Buscar producto por su ID
    ProductoResponse buscarPorId(Integer id);

    // Búsqueda por término (nombre o SKU)
    List<ProductoResponse> buscar(String termino);

    // Listar productos activos que tienen stock_actual <= stock_minimo
    List<ProductoResponse> listarBajoStock();

    // Crear un nuevo producto (siempre con activo = true)
    ProductoResponse crear(ProductoRequest request);

    // Actualizar un producto existente (NO modifica el estado activo)
    ProductoResponse actualizar(Integer id, ProductoRequest request);

    // Cambiar estado activo/inactivo (PATCH)
    ProductoResponse cambiarEstado(Integer id, EstadoRequest request);
}
