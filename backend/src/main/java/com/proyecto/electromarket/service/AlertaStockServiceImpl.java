package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.AlertaStockResponse;
import com.proyecto.electromarket.entity.Producto;
import com.proyecto.electromarket.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertaStockServiceImpl implements AlertaStockService {

    private final ProductoRepository productoRepository;

    public AlertaStockServiceImpl(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AlertaStockResponse> listarAlertasStock() {
        return productoRepository.findProductosAlertaStock()
                .stream()
                .map(this::convertirAAlertaResponse)
                .collect(Collectors.toList());
    }

    private AlertaStockResponse convertirAAlertaResponse(Producto p) {
        int actual = p.getStockActual() != null ? p.getStockActual() : 0;
        int minimo = p.getStockMinimo() != null ? p.getStockMinimo() : 0;
        int diferencia = minimo - actual; // Diferencia: cuánto falta para cubrir el mínimo (>= 0)

        return new AlertaStockResponse(
                p.getIdProducto(),
                p.getCodigoSku(),
                p.getNombre(),
                actual,
                minimo,
                diferencia
        );
    }
}
