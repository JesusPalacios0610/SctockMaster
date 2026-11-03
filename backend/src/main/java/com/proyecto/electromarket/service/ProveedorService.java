package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.ProveedorRequest;
import com.proyecto.electromarket.dto.ProveedorResponse;
import java.util.List;

public interface ProveedorService {

    // Listar todos los proveedores
    List<ProveedorResponse> listarTodos();

    // Buscar proveedor por su ID
    ProveedorResponse buscarPorId(Integer id);

    // Buscar proveedor por RUC
    ProveedorResponse buscarPorRuc(String ruc);

    // Crear un nuevo proveedor
    ProveedorResponse crear(ProveedorRequest request);

    // Actualizar un proveedor existente
    ProveedorResponse actualizar(Integer id, ProveedorRequest request);
}
