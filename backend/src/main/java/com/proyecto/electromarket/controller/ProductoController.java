package com.proyecto.electromarket.controller;

import com.proyecto.electromarket.dto.ProductoRequest;
import com.proyecto.electromarket.dto.ProductoResponse;
import com.proyecto.electromarket.service.ProductoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    // Inyección de dependencias por constructor
    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // GET /api/productos - Listar todos los productos
    @GetMapping
    public ResponseEntity<List<ProductoResponse>> listarTodos() {
        return ResponseEntity.ok(productoService.listarTodos());
    }

    // GET /api/productos/{id} - Obtener un producto por su ID
    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(productoService.buscarPorId(id));
    }

    // GET /api/productos/buscar?termino=laptop - Búsqueda por nombre o SKU
    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponse>> buscar(@RequestParam(required = false) String termino) {
        return ResponseEntity.ok(productoService.buscar(termino));
    }

    // GET /api/productos/alertas - Listar productos con stock crítico (bajo stock mínimo)
    @GetMapping("/alertas")
    public ResponseEntity<List<ProductoResponse>> listarBajoStock() {
        return ResponseEntity.ok(productoService.listarBajoStock());
    }

    // POST /api/productos - Crear un nuevo producto
    // @Valid activa las anotaciones de validación de ProductoRequest (@NotBlank, @NotNull, etc.)
    @PostMapping
    public ResponseEntity<ProductoResponse> crear(@Valid @RequestBody ProductoRequest request) {
        ProductoResponse creado = productoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    // PUT /api/productos/{id} - Actualizar un producto existente
    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponse> actualizar(
            @PathVariable Integer id,
            @Valid @RequestBody ProductoRequest request) {
        return ResponseEntity.ok(productoService.actualizar(id, request));
    }
}
