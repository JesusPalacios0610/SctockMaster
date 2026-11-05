package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.SalidaStockRequest;
import com.proyecto.electromarket.dto.SalidaStockResponse;
import com.proyecto.electromarket.entity.Producto;
import com.proyecto.electromarket.entity.SalidaStock;
import com.proyecto.electromarket.entity.Usuario;
import com.proyecto.electromarket.exception.RecursoNoEncontradoException;
import com.proyecto.electromarket.repository.ProductoRepository;
import com.proyecto.electromarket.repository.SalidaStockRepository;
import com.proyecto.electromarket.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SalidaStockServiceImpl implements SalidaStockService {

    // Tipos de salida permitidos según las reglas de negocio
    private static final Set<String> TIPOS_PERMITIDOS = Set.of("VENTA", "TRASLADO", "MERMA");

    private final SalidaStockRepository salidaStockRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public SalidaStockServiceImpl(SalidaStockRepository salidaStockRepository,
                                  ProductoRepository productoRepository,
                                  UsuarioRepository usuarioRepository) {
        this.salidaStockRepository = salidaStockRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional
    public SalidaStockResponse registrarSalida(SalidaStockRequest request) {
        // 1. Validar que la cantidad sea mayor que 0
        if (request.getCantidad() == null || request.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que 0");
        }

        // 2. Validar que tipoSalida sea VENTA, TRASLADO o MERMA (en mayúsculas)
        String tipo = request.getTipoSalida() != null ? request.getTipoSalida().trim().toUpperCase() : "";
        if (!TIPOS_PERMITIDOS.contains(tipo)) {
            throw new IllegalArgumentException("Tipo de salida inválido: '" + request.getTipoSalida() + "'. Valores permitidos: VENTA, TRASLADO, MERMA");
        }

        // 3. Validar existencia del Usuario
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario con ID: " + request.getIdUsuario()));

        // 4. Obtener el Producto con Bloqueo Pesimista (FOR UPDATE) para proteger la concurrencia
        Producto producto = productoRepository.findByIdConBloqueo(request.getIdProducto())
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el producto con ID: " + request.getIdProducto()));

        // 5. Validar que el stock NUNCA quede negativo
        int stockDisponible = producto.getStockActual() != null ? producto.getStockActual() : 0;
        if (request.getCantidad() > stockDisponible) {
            throw new IllegalArgumentException(
                    "Stock insuficiente: disponible " + stockDisponible + ", solicitado " + request.getCantidad()
            );
        }

        // 6. Restar la cantidad a producto.stock_actual
        int nuevoStock = stockDisponible - request.getCantidad();
        producto.setStockActual(nuevoStock);
        producto.setUsuarioActualizacion(request.getIdUsuario());
        productoRepository.save(producto);

        // 7. Crear y guardar la entidad SalidaStock
        SalidaStock salida = new SalidaStock();
        salida.setProducto(producto);
        salida.setUsuario(usuario);
        salida.setFecha(request.getFecha() != null ? request.getFecha() : LocalDate.now());
        salida.setTipoSalida(tipo);
        salida.setCantidad(request.getCantidad());

        SalidaStock salidaGuardada = salidaStockRepository.save(salida);

        return convertirAResponse(salidaGuardada, nuevoStock);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SalidaStockResponse> listarTodas() {
        return salidaStockRepository.findAllByOrderByFechaDesc()
                .stream()
                .map(s -> convertirAResponse(s, s.getProducto().getStockActual()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SalidaStockResponse buscarPorId(Integer id) {
        SalidaStock salida = salidaStockRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró la salida de stock con ID: " + id));

        return convertirAResponse(salida, salida.getProducto().getStockActual());
    }

    // Auxiliar para mapear a SalidaStockResponse DTO
    private SalidaStockResponse convertirAResponse(SalidaStock s, Integer stockRestante) {
        return new SalidaStockResponse(
                s.getIdSalida(),
                s.getProducto().getIdProducto(),
                s.getProducto().getCodigoSku(),
                s.getProducto().getNombre(),
                s.getUsuario().getIdUsuario(),
                s.getUsuario().getNombre(),
                s.getFecha(),
                s.getTipoSalida(),
                s.getCantidad(),
                stockRestante,
                s.getFechaCreacion()
        );
    }
}
