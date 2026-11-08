package com.proyecto.electromarket.controller;

import com.proyecto.electromarket.dto.MovimientoResponse;
import com.proyecto.electromarket.service.MovimientoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    // GET /api/movimientos?idProducto=&desde=&hasta= -> Lista unificada de movimientos con saldo
    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listarMovimientos(
            @RequestParam(required = false) Integer idProducto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(movimientoService.listarMovimientos(idProducto, desde, hasta));
    }
}
