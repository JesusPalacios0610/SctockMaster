package com.proyecto.electromarket.controller;

import com.proyecto.electromarket.dto.AlertaStockResponse;
import com.proyecto.electromarket.service.AlertaStockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/alertas-stock")
public class AlertaStockController {

    private final AlertaStockService alertaStockService;

    public AlertaStockController(AlertaStockService alertaStockService) {
        this.alertaStockService = alertaStockService;
    }

    // GET /api/alertas-stock -> Lista productos con stock crítico (stock_actual <= stock_minimo) ordenados de menor a mayor
    @GetMapping
    public ResponseEntity<List<AlertaStockResponse>> listarAlertas() {
        return ResponseEntity.ok(alertaStockService.listarAlertasStock());
    }
}
