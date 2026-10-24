package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.ProductoRequest;
import com.proyecto.electromarket.dto.ProductoResponse;
import java.util.List;

public interface ProductoService {

    // Obtener todos los productos
    List<ProductoResponse> listarTodos();

    // Buscar producto por su ID
    ProductoResponse buscarPorId(Integer id);

    // Búsqueda por término (nombre o SKU)
    List<ProductoResponse> buscar(String termino);

    // Listar productos que tienen stock_actual <= stock_minimo
    List<ProductoResponse> listarBajoStock();

    // Crear un nuevo producto
    ProductoResponse crear(ProductoRequest request);

    // Actualizar un producto existente
    ProductoResponse actualizar(Integer id, ProductoRequest request);
}
