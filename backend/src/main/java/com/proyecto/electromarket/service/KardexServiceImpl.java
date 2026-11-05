package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.KardexResponse;
import com.proyecto.electromarket.entity.Kardex;
import com.proyecto.electromarket.entity.Producto;
import com.proyecto.electromarket.exception.RecursoNoEncontradoException;
import com.proyecto.electromarket.repository.DetalleEntradaRepository;
import com.proyecto.electromarket.repository.KardexRepository;
import com.proyecto.electromarket.repository.ProductoRepository;
import com.proyecto.electromarket.repository.SalidaStockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KardexServiceImpl implements KardexService {

    private final KardexRepository kardexRepository;
    private final ProductoRepository productoRepository;
    private final DetalleEntradaRepository detalleEntradaRepository;
    private final SalidaStockRepository salidaStockRepository;

    public KardexServiceImpl(KardexRepository kardexRepository,
                             ProductoRepository productoRepository,
                             DetalleEntradaRepository detalleEntradaRepository,
                             SalidaStockRepository salidaStockRepository) {
        this.kardexRepository = kardexRepository;
        this.productoRepository = productoRepository;
        this.detalleEntradaRepository = detalleEntradaRepository;
        this.salidaStockRepository = salidaStockRepository;
    }

    @Override
    @Transactional
    public List<KardexResponse> generarKardexHoy() {
        LocalDate hoy = LocalDate.now();
        List<Producto> productos = productoRepository.findAll();
        List<KardexResponse> respuestas = new ArrayList<>();

        for (Producto producto : productos) {
            Integer idProducto = producto.getIdProducto();

            // 1. Sumar entradas del día (con COALESCE devuelve 0 si no hay registros)
            int entradasHoy = detalleEntradaRepository.sumarEntradasPorProductoYFecha(idProducto, hoy);

            // 2. Sumar salidas del día (con COALESCE devuelve 0 si no hay registros)
            int salidasHoy = salidaStockRepository.sumarSalidasPorProductoYFecha(idProducto, hoy);

            // 3. Fórmulas de balance
            int stockFinal = producto.getStockActual() != null ? producto.getStockActual() : 0;
            int stockInicial = stockFinal - entradasHoy + salidasHoy;

            // 4. Buscar si ya existe registro para hoy (idempotente: actualiza en vez de duplicar)
            Optional<Kardex> kardexExistente = kardexRepository.findByProductoIdProductoAndFechaCalculo(idProducto, hoy);

            Kardex kardex;
            if (kardexExistente.isPresent()) {
                kardex = kardexExistente.get();
                kardex.setStockInicial(stockInicial);
                kardex.setEntradas(entradasHoy);
                kardex.setSalidas(salidasHoy);
                kardex.setStockFinal(stockFinal);
            } else {
                kardex = new Kardex();
                kardex.setProducto(producto);
                kardex.setFechaCalculo(hoy);
                kardex.setStockInicial(stockInicial);
                kardex.setEntradas(entradasHoy);
                kardex.setSalidas(salidasHoy);
                kardex.setStockFinal(stockFinal);
            }

            Kardex guardado = kardexRepository.save(kardex);
            respuestas.add(convertirAKardexResponse(guardado));
        }

        return respuestas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KardexResponse> consultarKardex(Integer idProducto, LocalDate desde, LocalDate hasta) {
        List<Kardex> resultados;

        if (idProducto != null) {
            // Validar que el producto exista si se pasó como parámetro
            if (!productoRepository.existsById(idProducto)) {
                throw new RecursoNoEncontradoException("No se encontró el producto con ID: " + idProducto);
            }
            resultados = kardexRepository.filtrarPorProductoYFechas(idProducto, desde, hasta);
        } else {
            // Si idProducto no viene, devolver kardex de todos los productos en el rango de fechas
            resultados = kardexRepository.filtrarSoloPorFechas(desde, hasta);
        }

        return resultados.stream()
                .map(this::convertirAKardexResponse)
                .collect(Collectors.toList());
    }

    private KardexResponse convertirAKardexResponse(Kardex k) {
        return new KardexResponse(
                k.getIdKardex(),
                k.getProducto().getIdProducto(),
                k.getProducto().getCodigoSku(),
                k.getProducto().getNombre(),
                k.getFechaCalculo(),
                k.getStockInicial(),
                k.getEntradas(),
                k.getSalidas(),
                k.getStockFinal(),
                k.getFechaCreacion()
        );
    }
}
