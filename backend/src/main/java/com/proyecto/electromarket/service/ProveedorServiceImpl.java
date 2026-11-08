package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.EstadoRequest;
import com.proyecto.electromarket.dto.ProveedorRequest;
import com.proyecto.electromarket.dto.ProveedorResponse;
import com.proyecto.electromarket.entity.Proveedor;
import com.proyecto.electromarket.exception.RecursoNoEncontradoException;
import com.proyecto.electromarket.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    public ProveedorServiceImpl(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProveedorResponse> listarTodos() {
        return proveedorRepository.findAll()
                .stream()
                .map(this::convertirAProveedorResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse buscarPorId(Integer id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el proveedor con ID: " + id));
        return convertirAProveedorResponse(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse buscarPorRuc(String ruc) {
        String rucLimpio = ruc != null ? ruc.trim() : "";
        Proveedor proveedor = proveedorRepository.findByRuc(rucLimpio)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el proveedor con RUC: " + rucLimpio));
        return convertirAProveedorResponse(proveedor);
    }

    @Override
    @Transactional
    public ProveedorResponse crear(ProveedorRequest request) {
        String rucLimpio = request.getRuc().trim();

        // Regla de negocio: El RUC debe ser único
        if (proveedorRepository.existsByRuc(rucLimpio)) {
            throw new IllegalArgumentException("Ya existe un proveedor registrado con el RUC: " + rucLimpio);
        }

        Proveedor nuevo = new Proveedor();
        nuevo.setRuc(rucLimpio);
        nuevo.setRazonSocial(request.getRazonSocial().trim());
        nuevo.setContacto(limpiarTexto(request.getContacto()));
        nuevo.setTelefono(limpiarTexto(request.getTelefono()));
        nuevo.setCorreo(limpiarTexto(request.getCorreo()));
        nuevo.setTiempoEntrega(request.getTiempoEntrega());
        nuevo.setActivo(true); // Siempre activo al crearse
        nuevo.setUsuarioActualizacion(request.getIdUsuario());

        Proveedor guardado = proveedorRepository.save(nuevo);
        return convertirAProveedorResponse(guardado);
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Integer id, ProveedorRequest request) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el proveedor con ID: " + id));

        String nuevoRuc = request.getRuc().trim();

        // Si el RUC cambia, validamos que no pertenezca a otro proveedor
        if (!existente.getRuc().equalsIgnoreCase(nuevoRuc) && proveedorRepository.existsByRuc(nuevoRuc)) {
            throw new IllegalArgumentException("Ya existe otro proveedor registrado con el RUC: " + nuevoRuc);
        }

        existente.setRuc(nuevoRuc);
        existente.setRazonSocial(request.getRazonSocial().trim());
        existente.setContacto(limpiarTexto(request.getContacto()));
        existente.setTelefono(limpiarTexto(request.getTelefono()));
        existente.setCorreo(limpiarTexto(request.getCorreo()));
        existente.setTiempoEntrega(request.getTiempoEntrega());
        // El PUT NO modifica "activo"
        existente.setUsuarioActualizacion(request.getIdUsuario());

        Proveedor actualizado = proveedorRepository.save(existente);
        return convertirAProveedorResponse(actualizado);
    }

    @Override
    @Transactional
    public ProveedorResponse cambiarEstado(Integer id, EstadoRequest request) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el proveedor con ID: " + id));

        proveedor.setActivo(request.getActivo());
        // Solo actualizar usuarioActualizacion si idUsuario viene con valor
        if (request.getIdUsuario() != null) {
            proveedor.setUsuarioActualizacion(request.getIdUsuario());
        }

        Proveedor actualizado = proveedorRepository.save(proveedor);
        return convertirAProveedorResponse(actualizado);
    }

    // Auxiliar para aplicar trim y guardar null si viene vacío o null
    private String limpiarTexto(String texto) {
        if (texto == null) {
            return null;
        }
        String limpio = texto.trim();
        return limpio.isEmpty() ? null : limpio;
    }

    // Conversión de Entidad Proveedor a ProveedorResponse DTO
    private ProveedorResponse convertirAProveedorResponse(Proveedor p) {
        return new ProveedorResponse(
                p.getIdProveedor(),
                p.getRuc(),
                p.getRazonSocial(),
                p.getContacto(),
                p.getTelefono(),
                p.getCorreo(),
                p.getTiempoEntrega(),
                p.getActivo(),
                p.getFechaCreacion(),
                p.getFechaActualizacion()
        );
    }
}
