package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.AlertaStockResponse;
import java.util.List;

public interface AlertaStockService {

    // Listar productos con stock_actual <= stock_minimo ordenados de menor a mayor stock
    List<AlertaStockResponse> listarAlertasStock();
}
