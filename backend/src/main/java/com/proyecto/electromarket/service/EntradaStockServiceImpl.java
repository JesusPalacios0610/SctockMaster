package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.DetalleEntradaRequest;
import com.proyecto.electromarket.dto.DetalleEntradaResponse;
import com.proyecto.electromarket.dto.EntradaStockRequest;
import com.proyecto.electromarket.dto.EntradaStockResponse;
import com.proyecto.electromarket.entity.DetalleEntrada;
import com.proyecto.electromarket.entity.EntradaStock;
import com.proyecto.electromarket.entity.Producto;
import com.proyecto.electromarket.entity.Proveedor;
import com.proyecto.electromarket.entity.Usuario;
import com.proyecto.electromarket.exception.RecursoNoEncontradoException;
import com.proyecto.electromarket.repository.DetalleEntradaRepository;
import com.proyecto.electromarket.repository.EntradaStockRepository;
import com.proyecto.electromarket.repository.ProductoRepository;
import com.proyecto.electromarket.repository.ProveedorRepository;
import com.proyecto.electromarket.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntradaStockServiceImpl implements EntradaStockService {

    private final EntradaStockRepository entradaStockRepository;
    private final DetalleEntradaRepository detalleEntradaRepository;
    private final ProductoRepository productoRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;

    public EntradaStockServiceImpl(EntradaStockRepository entradaStockRepository,
                                  DetalleEntradaRepository detalleEntradaRepository,
                                  ProductoRepository productoRepository,
                                  ProveedorRepository proveedorRepository,
                                  UsuarioRepository usuarioRepository) {
        this.entradaStockRepository = entradaStockRepository;
        this.detalleEntradaRepository = detalleEntradaRepository;
        this.productoRepository = productoRepository;
        this.proveedorRepository = proveedorRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public EntradaStockResponse registrarEntrada(EntradaStockRequest request) {
        // 1. Validar que la lista de detalles no esté vacía
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La entrada debe contener al menos una línea de producto");
        }

        // 2. Validar que exista el Proveedor
        Proveedor proveedor = proveedorRepository.findById(request.getIdProveedor())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el proveedor con ID: " + request.getIdProveedor()));

        // 3. Validar que exista el Usuario
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario con ID: " + request.getIdUsuario()));

        // 4. Crear y guardar la cabecera (EntradaStock) con estado 'REGISTRADA'
        EntradaStock entrada = new EntradaStock();
        entrada.setProveedor(proveedor);
        entrada.setUsuario(usuario);
        entrada.setDocumento(request.getDocumento().trim());
        entrada.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        entrada.setEstado("REGISTRADA");
        entrada.setUsuarioActualizacion(request.getIdUsuario());

        EntradaStock entradaGuardada = entradaStockRepository.save(entrada);

        // 5. Procesar cada detalle y sumar el stock del producto
        List<DetalleEntradaResponse> detallesResponse = new ArrayList<>();

        for (DetalleEntradaRequest detalleReq : request.getDetalles()) {
            if (detalleReq.getCantidad() == null || detalleReq.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que 0 para todos los productos");
            }

            // Validar que el producto exista
            Producto producto = productoRepository.findById(detalleReq.getIdProducto())
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el producto con ID: " + detalleReq.getIdProducto()));

            // Crear y guardar el detalle
            DetalleEntrada detalle = new DetalleEntrada();
            detalle.setEntrada(entradaGuardada);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleReq.getCantidad());
            DetalleEntrada detalleGuardado = detalleEntradaRepository.save(detalle);

            // Actualizar el stock actual del producto (suma transaccional)
            int stockAnterior = producto.getStockActual() != null ? producto.getStockActual() : 0;
            producto.setStockActual(stockAnterior + detalleReq.getCantidad());
            producto.setUsuarioActualizacion(request.getIdUsuario());
            productoRepository.save(producto);

            // Agregar a la lista de respuestas
            detallesResponse.add(new DetalleEntradaResponse(
                    detalleGuardado.getIdDetalle(),
                    producto.getIdProducto(),
                    producto.getCodigoSku(),
                    producto.getNombre(),
                    detalleGuardado.getCantidad()
            ));
        }

        return construirEntradaResponse(entradaGuardada, detallesResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EntradaStockResponse> listarTodas() {
        return entradaStockRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(entrada -> {
                    List<DetalleEntradaResponse> detalles = detalleEntradaRepository
                            .findByEntradaIdEntrada(entrada.getIdEntrada())
                            .stream()
                            .map(this::convertirADetalleResponse)
                            .collect(Collectors.toList());
                    return construirEntradaResponse(entrada, detalles);
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public EntradaStockResponse buscarPorId(Integer id) {
        EntradaStock entrada = entradaStockRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la entrada de stock con ID: " + id));

        List<DetalleEntradaResponse> detalles = detalleEntradaRepository
                .findByEntradaIdEntrada(entrada.getIdEntrada())
                .stream()
                .map(this::convertirADetalleResponse)
                .collect(Collectors.toList());

        return construirEntradaResponse(entrada, detalles);
    }

    // Métodos auxiliares de mapeo DTO
    private DetalleEntradaResponse convertirADetalleResponse(DetalleEntrada d) {
        return new DetalleEntradaResponse(
                d.getIdDetalle(),
                d.getProducto().getIdProducto(),
                d.getProducto().getCodigoSku(),
                d.getProducto().getNombre(),
                d.getCantidad()
        );
    }

    private EntradaStockResponse construirEntradaResponse(EntradaStock entrada, List<DetalleEntradaResponse> detalles) {
        return new EntradaStockResponse(
                entrada.getIdEntrada(),
                entrada.getProveedor().getIdProveedor(),
                entrada.getProveedor().getRazonSocial(),
                entrada.getUsuario().getIdUsuario(),
                entrada.getUsuario().getNombre(),
                entrada.getDocumento(),
                entrada.getFecha(),
                entrada.getEstado(),
                entrada.getFechaCreacion(),
                detalles
        );
    }
}
