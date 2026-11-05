package com.proyecto.electromarket.controller;

import com.proyecto.electromarket.dto.SalidaStockRequest;
import com.proyecto.electromarket.dto.SalidaStockResponse;
import com.proyecto.electromarket.service.SalidaStockService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/salidas")
public class SalidaStockController {

    private final SalidaStockService salidaStockService;

    public SalidaStockController(SalidaStockService salidaStockService) {
        this.salidaStockService = salidaStockService;
    }

    // POST /api/salidas -> Registra una salida de stock restando el stock de forma transaccional
    @PostMapping
    public ResponseEntity<SalidaStockResponse> registrar(@Valid @RequestBody SalidaStockRequest request) {
        SalidaStockResponse creada = salidaStockService.registrarSalida(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // GET /api/salidas -> Lista todas las salidas ordenadas por fecha descendente
    @GetMapping
    public ResponseEntity<List<SalidaStockResponse>> listarTodas() {
        return ResponseEntity.ok(salidaStockService.listarTodas());
    }

    // GET /api/salidas/{id} -> Detalle de una salida por su ID
    @GetMapping("/{id}")
    public ResponseEntity<SalidaStockResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(salidaStockService.buscarPorId(id));
    }
}
