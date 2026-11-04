package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.EntradaStockRequest;
import com.proyecto.electromarket.dto.EntradaStockResponse;
import java.util.List;

public interface EntradaStockService {

    // Registrar una entrada de stock con cabecera y detalle de forma transaccional
    EntradaStockResponse registrarEntrada(EntradaStockRequest request);

    // Listar todas las entradas de stock ordenadas por fecha descendente
    List<EntradaStockResponse> listarTodas();

    // Obtener una entrada por ID con sus líneas detalladas
    EntradaStockResponse buscarPorId(Integer id);
}
