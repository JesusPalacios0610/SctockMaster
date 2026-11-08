package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.MovimientoResponse;
import java.time.LocalDate;
import java.util.List;

public interface MovimientoService {

    // Listar movimientos con cálculo de saldo hacia atrás
    List<MovimientoResponse> listarMovimientos(Integer idProducto, LocalDate desde, LocalDate hasta);
}
