package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.MovimientoResponse;
import com.proyecto.electromarket.entity.DetalleEntrada;
import com.proyecto.electromarket.entity.Producto;
import com.proyecto.electromarket.entity.SalidaStock;
import com.proyecto.electromarket.exception.RecursoNoEncontradoException;
import com.proyecto.electromarket.repository.DetalleEntradaRepository;
import com.proyecto.electromarket.repository.ProductoRepository;
import com.proyecto.electromarket.repository.SalidaStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MovimientoServiceImpl implements MovimientoService {

    private final ProductoRepository productoRepository;
    private final DetalleEntradaRepository detalleEntradaRepository;
    private final SalidaStockRepository salidaStockRepository;

    public MovimientoServiceImpl(ProductoRepository productoRepository,
                                 DetalleEntradaRepository detalleEntradaRepository,
                                 SalidaStockRepository salidaStockRepository) {
        this.productoRepository = productoRepository;
        this.detalleEntradaRepository = detalleEntradaRepository;
        this.salidaStockRepository = salidaStockRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listarMovimientos(Integer idProducto, LocalDate desde, LocalDate hasta) {
        // 1. Validar fechas: desde no puede ser posterior a hasta
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException("La fecha inicial ('desde') no puede ser posterior a la fecha final ('hasta')");
        }

        // 2. Si se solicitó un producto específico
        if (idProducto != null) {
            Producto producto = productoRepository.findById(idProducto)
                    .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el producto con ID: " + idProducto));

            List<DetalleEntrada> detalles = detalleEntradaRepository.findByProductoIdConRelaciones(idProducto);
            List<SalidaStock> salidas = salidaStockRepository.findByProductoIdConRelaciones(idProducto);

            List<MovimientoResponse> movimientosCalculados = calcularMovimientosDeProducto(producto, detalles, salidas);

            // Filtrar por rango de fechas
            return filtrarPorRango(movimientosCalculados, desde, hasta);
        }

        // 3. Si no se pasó idProducto: calcular para todos los productos
        List<Producto> productos = productoRepository.findAll();
        List<DetalleEntrada> todosDetalles = detalleEntradaRepository.findAllConRelaciones();
        List<SalidaStock> todasSalidas = salidaStockRepository.findAllConRelaciones();

        // Agrupar entradas y salidas por idProducto
        Map<Integer, List<DetalleEntrada>> detallesPorProducto = todosDetalles.stream()
                .collect(Collectors.groupingBy(d -> d.getProducto().getIdProducto()));

        Map<Integer, List<SalidaStock>> salidasPorProducto = todasSalidas.stream()
                .collect(Collectors.groupingBy(s -> s.getProducto().getIdProducto()));

        List<MovimientoResponse> todosLosMovimientos = new ArrayList<>();

        for (Producto producto : productos) {
            List<DetalleEntrada> dets = detallesPorProducto.getOrDefault(producto.getIdProducto(), List.of());
            List<SalidaStock> sals = salidasPorProducto.getOrDefault(producto.getIdProducto(), List.of());

            List<MovimientoResponse> movsProducto = calcularMovimientosDeProducto(producto, dets, sals);
            todosLosMovimientos.addAll(movsProducto);
        }

        // Filtrar por rango de fechas y ordenar de más antiguo a más reciente
        return filtrarPorRango(todosLosMovimientos, desde, hasta)
                .stream()
                .sorted(Comparator.comparing(MovimientoResponse::getFecha)
                        .thenComparing(MovimientoResponse::getFechaCreacion, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    /**
     * Calcula los saldos hacia atrás para un producto a partir de su stockActual.
     */
    private List<MovimientoResponse> calcularMovimientosDeProducto(Producto producto,
                                                                  List<DetalleEntrada> detalles,
                                                                  List<SalidaStock> salidas) {
        List<MovimientoResponse> unificados = new ArrayList<>();

        // Mapear entradas
        for (DetalleEntrada d : detalles) {
            unificados.add(new MovimientoResponse(
                    "ENT-" + d.getIdDetalle(),
                    d.getEntrada().getFecha(),
                    "Entrada",
                    d.getEntrada().getDocumento(),
                    d.getEntrada().getTipoDocumento(),
                    producto.getIdProducto(),
                    producto.getCodigoSku(),
                    producto.getNombre(),
                    d.getCantidad(),
                    0, // Saldo temporal
                    d.getEntrada().getUsuario().getNombre(),
                    d.getFechaCreacion()
            ));
        }

        // Mapear salidas
        for (SalidaStock s : salidas) {
            unificados.add(new MovimientoResponse(
                    "SAL-" + s.getIdSalida(),
                    s.getFecha(),
                    "Salida",
                    s.getDocumento(),
                    s.getTipoSalida(),
                    producto.getIdProducto(),
                    producto.getCodigoSku(),
                    producto.getNombre(),
                    s.getCantidad(),
                    0, // Saldo temporal
                    s.getUsuario().getNombre(),
                    s.getFechaCreacion()
            ));
        }

        // Ordenar cronológicamente de más antiguo a más reciente
        unificados.sort(Comparator.comparing(MovimientoResponse::getFecha)
                .thenComparing(MovimientoResponse::getFechaCreacion, Comparator.nullsLast(Comparator.naturalOrder())));

        if (unificados.isEmpty()) {
            return unificados;
        }

        // CÁLCULO HACIA ATRÁS DESDE producto.stockActual
        int n = unificados.size();
        int stockActual = producto.getStockActual() != null ? producto.getStockActual() : 0;

        // El saldo del último movimiento es exactamente el stock actual
        unificados.get(n - 1).setSaldo(stockActual);

        // Para los anteriores:
        // Si el siguiente fue Entrada (+), antes había menos -> saldo(i) = saldo(i+1) - cantidad(i+1)
        // Si el siguiente fue Salida (-), antes había más -> saldo(i) = saldo(i+1) + cantidad(i+1)
        for (int i = n - 2; i >= 0; i--) {
            MovimientoResponse siguiente = unificados.get(i + 1);
            int saldoSiguiente = siguiente.getSaldo();

            if ("Entrada".equalsIgnoreCase(siguiente.getTipo())) {
                unificados.get(i).setSaldo(saldoSiguiente - siguiente.getCantidad());
            } else {
                unificados.get(i).setSaldo(saldoSiguiente + siguiente.getCantidad());
            }
        }

        return unificados;
    }

    private List<MovimientoResponse> filtrarPorRango(List<MovimientoResponse> lista, LocalDate desde, LocalDate hasta) {
        return lista.stream()
                .filter(m -> desde == null || !m.getFecha().isBefore(desde))
                .filter(m -> hasta == null || !m.getFecha().isAfter(hasta))
                .collect(Collectors.toList());
    }
}
