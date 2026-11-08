package com.proyecto.electromarket.controller;

import com.proyecto.electromarket.dto.EstadoRequest;
import com.proyecto.electromarket.dto.ProveedorRequest;
import com.proyecto.electromarket.dto.ProveedorResponse;
import com.proyecto.electromarket.service.ProveedorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    // GET /api/proveedores - Listar todos los proveedores
    @GetMapping
    public ResponseEntity<List<ProveedorResponse>> listarTodos() {
        return ResponseEntity.ok(proveedorService.listarTodos());
    }

    // GET /api/proveedores/{id} - Obtener un proveedor por ID
    @GetMapping("/{id}")
    public ResponseEntity<ProveedorResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(proveedorService.buscarPorId(id));
    }

    // GET /api/proveedores/ruc/{ruc} - Obtener un proveedor por su RUC
    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<ProveedorResponse> buscarPorRuc(@PathVariable String ruc) {
        return ResponseEntity.ok(proveedorService.buscarPorRuc(ruc));
    }

    // POST /api/proveedores - Crear un nuevo proveedor
    @PostMapping
    public ResponseEntity<ProveedorResponse> crear(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse creado = proveedorService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // PUT /api/proveedores/{id} - Actualizar un proveedor existente (no modifica estado activo)
    @PutMapping("/{id}")
    public ResponseEntity<ProveedorResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProveedorRequest request) {
        return ResponseEntity.ok(proveedorService.actualizar(id, request));
    }

    // PATCH /api/proveedores/{id}/estado - Cambiar estado activo/inactivo
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ProveedorResponse> cambiarEstado(
            @PathVariable Integer id,
            @Valid @RequestBody EstadoRequest request) {
        return ResponseEntity.ok(proveedorService.cambiarEstado(id, request));
    }
}
