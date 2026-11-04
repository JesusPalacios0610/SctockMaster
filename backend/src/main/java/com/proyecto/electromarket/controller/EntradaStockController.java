package com.proyecto.electromarket.controller;

import com.proyecto.electromarket.dto.EntradaStockRequest;
import com.proyecto.electromarket.dto.EntradaStockResponse;
import com.proyecto.electromarket.service.EntradaStockService;
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
@RequestMapping("/api/entradas")
public class EntradaStockController {

    private final EntradaStockService entradaStockService;

    public EntradaStockController(EntradaStockService entradaStockService) {
        this.entradaStockService = entradaStockService;
    }

    // POST /api/entradas -> Registra una entrada con cabecera y detalle de forma transaccional
    @PostMapping
    public ResponseEntity<EntradaStockResponse> registrar(@Valid @RequestBody EntradaStockRequest request) {
        EntradaStockResponse creada = entradaStockService.registrarEntrada(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // GET /api/entradas -> Lista todas las entradas ordenadas por fecha descendente
    @GetMapping
    public ResponseEntity<List<EntradaStockResponse>> listarTodas() {
        return ResponseEntity.ok(entradaStockService.listarTodas());
    }

    // GET /api/entradas/{id} -> Detalle de una entrada específica con sus líneas de producto
    @GetMapping("/{id}")
    public ResponseEntity<EntradaStockResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(entradaStockService.buscarPorId(id));
    }
}
