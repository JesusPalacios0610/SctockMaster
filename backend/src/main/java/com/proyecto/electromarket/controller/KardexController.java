package com.proyecto.electromarket.controller;

import com.proyecto.electromarket.dto.KardexResponse;
import com.proyecto.electromarket.service.KardexService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/kardex")
public class KardexController {

    private final KardexService kardexService;

    public KardexController(KardexService kardexService) {
        this.kardexService = kardexService;
    }

    // POST /api/kardex/generar -> Calcula y guarda/actualiza el kardex de todos los productos para la fecha de hoy
    @PostMapping("/generar")
    public ResponseEntity<List<KardexResponse>> generarKardexHoy() {
        List<KardexResponse> resultado = kardexService.generarKardexHoy();
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // GET /api/kardex?idProducto=&desde=&hasta= -> Consulta el kardex (parámetros opcionales)
    @GetMapping
    public ResponseEntity<List<KardexResponse>> consultarKardex(
            @RequestParam(required = false) Integer idProducto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(kardexService.consultarKardex(idProducto, desde, hasta));
    }
}
