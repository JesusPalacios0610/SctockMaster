package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.EstadoRequest;
import com.proyecto.electromarket.dto.ProveedorRequest;
import com.proyecto.electromarket.dto.ProveedorResponse;
import java.util.List;

public interface ProveedorService {

    // Listar todos los proveedores (activos e inactivos)
    List<ProveedorResponse> listarTodos();

    // Buscar proveedor por su ID
    ProveedorResponse buscarPorId(Integer id);

    // Buscar proveedor por RUC
    ProveedorResponse buscarPorRuc(String ruc);

    // Crear un nuevo proveedor (siempre activo = true)
    ProveedorResponse crear(ProveedorRequest request);

    // Actualizar un proveedor existente (no modifica estado activo)
    ProveedorResponse actualizar(Integer id, ProveedorRequest request);

    // Cambiar estado activo/inactivo (PATCH)
    ProveedorResponse cambiarEstado(Integer id, EstadoRequest request);
}
