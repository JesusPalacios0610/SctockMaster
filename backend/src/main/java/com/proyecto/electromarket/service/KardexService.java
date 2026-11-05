package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.KardexResponse;
import java.time.LocalDate;
import java.util.List;

public interface KardexService {

    // Calcular y guardar/actualizar el kardex de todos los productos para la fecha actual
    List<KardexResponse> generarKardexHoy();

    // Consultar kardex por producto (opcional) y rango de fechas (opcional)
    List<KardexResponse> consultarKardex(Integer idProducto, LocalDate desde, LocalDate hasta);
}
