package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.SalidaStockRequest;
import com.proyecto.electromarket.dto.SalidaStockResponse;
import java.util.List;

public interface SalidaStockService {

    // Registrar una salida de stock de un producto validando existencias de forma transaccional
    SalidaStockResponse registrarSalida(SalidaStockRequest request);

    // Listar todas las salidas ordenadas por fecha descendente
    List<SalidaStockResponse> listarTodas();

    // Obtener una salida por su ID
    SalidaStockResponse buscarPorId(Integer id);
}
