package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.ProductoRequest;
import com.proyecto.electromarket.dto.ProductoResponse;
import com.proyecto.electromarket.entity.Producto;
import com.proyecto.electromarket.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;

    // Inyección de dependencias por constructor (buena práctica de Spring)
    public ProductoServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarTodos() {
        return productoRepository.findAll()
                .stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse buscarPorId(Integer id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el producto con ID: " + id));
        return convertirAProductoResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> buscar(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return listarTodos();
        }
        String texto = termino.trim();
        return productoRepository.findByNombreContainingIgnoreCaseOrCodigoSkuContainingIgnoreCase(texto, texto)
                .stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> listarBajoStock() {
        return productoRepository.findProductosConBajoStock()
                .stream()
                .map(this::convertirAProductoResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProductoResponse crear(ProductoRequest request) {
        // Regla de negocio: El código SKU debe ser único
        String sku = request.getCodigoSku().trim();
        if (productoRepository.existsByCodigoSku(sku)) {
            throw new IllegalArgumentException("Ya existe un producto con el código SKU: " + sku);
        }

        Producto nuevo = new Producto();
        nuevo.setCodigoSku(sku);
        nuevo.setNombre(request.getNombre().trim());
        nuevo.setCategoria(request.getCategoria().trim());
        nuevo.setMarca(request.getMarca() != null ? request.getMarca().trim() : null);
        nuevo.setPrecio(request.getPrecio());
        nuevo.setUbicacion(request.getUbicacion() != null ? request.getUbicacion().trim() : null);
        nuevo.setStockMinimo(request.getStockMinimo());
        nuevo.setStockActual(request.getStockActual());
        nuevo.setUsuarioActualizacion(request.getIdUsuario());

        Producto guardado = productoRepository.save(nuevo);
        return convertirAProductoResponse(guardado);
    }

    @Override
    @Transactional
    public ProductoResponse actualizar(Integer id, ProductoRequest request) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el producto con ID: " + id));

        String nuevoSku = request.getCodigoSku().trim();
        // Si el SKU cambió, validamos que no esté en uso por otro producto
        if (!existente.getCodigoSku().equalsIgnoreCase(nuevoSku) && productoRepository.existsByCodigoSku(nuevoSku)) {
            throw new IllegalArgumentException("Ya existe otro producto con el código SKU: " + nuevoSku);
        }

        existente.setCodigoSku(nuevoSku);
        existente.setNombre(request.getNombre().trim());
        existente.setCategoria(request.getCategoria().trim());
        existente.setMarca(request.getMarca() != null ? request.getMarca().trim() : null);
        existente.setPrecio(request.getPrecio());
        existente.setUbicacion(request.getUbicacion() != null ? request.getUbicacion().trim() : null);
        existente.setStockMinimo(request.getStockMinimo());
        existente.setStockActual(request.getStockActual());
        // Enviamos el usuario que actualiza para la auditoría de la BD
        existente.setUsuarioActualizacion(request.getIdUsuario());

        Producto actualizado = productoRepository.save(existente);
        return convertirAProductoResponse(actualizado);
    }

    // Método auxiliar para transformar Entidad Producto -> ProductoResponse DTO
    private ProductoResponse convertirAProductoResponse(Producto p) {
        boolean alerta = p.getStockActual() <= p.getStockMinimo();
        return new ProductoResponse(
                p.getIdProducto(),
                p.getCodigoSku(),
                p.getNombre(),
                p.getCategoria(),
                p.getMarca(),
                p.getPrecio(),
                p.getUbicacion(),
                p.getStockMinimo(),
                p.getStockActual(),
                alerta,
                p.getFechaCreacion(),
                p.getFechaActualizacion()
        );
    }
}
