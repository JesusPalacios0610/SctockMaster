package com.proyecto.electromarket.service;

import com.proyecto.electromarket.dto.ProveedorRequest;
import com.proyecto.electromarket.dto.ProveedorResponse;
import com.proyecto.electromarket.entity.Proveedor;
import com.proyecto.electromarket.repository.ProveedorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    private final ProveedorRepository proveedorRepository;

    // Inyección de dependencias por constructor
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
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el proveedor con ID: " + id));
        return convertirAProveedorResponse(proveedor);
    }

    @Override
    @Transactional(readOnly = true)
    public ProveedorResponse buscarPorRuc(String ruc) {
        String rucLimpio = ruc != null ? ruc.trim() : "";
        Proveedor proveedor = proveedorRepository.findByRuc(rucLimpio)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el proveedor con RUC: " + rucLimpio));
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
        nuevo.setContacto(request.getContacto() != null ? request.getContacto().trim() : null);
        nuevo.setTiempoEntrega(request.getTiempoEntrega());
        nuevo.setUsuarioActualizacion(request.getIdUsuario());

        Proveedor guardado = proveedorRepository.save(nuevo);
        return convertirAProveedorResponse(guardado);
    }

    @Override
    @Transactional
    public ProveedorResponse actualizar(Integer id, ProveedorRequest request) {
        Proveedor existente = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el proveedor con ID: " + id));

        String nuevoRuc = request.getRuc().trim();

        // Si el RUC cambia, validamos que no pertenezca a otro proveedor
        if (!existente.getRuc().equalsIgnoreCase(nuevoRuc) && proveedorRepository.existsByRuc(nuevoRuc)) {
            throw new IllegalArgumentException("Ya existe otro proveedor registrado con el RUC: " + nuevoRuc);
        }

        existente.setRuc(nuevoRuc);
        existente.setRazonSocial(request.getRazonSocial().trim());
        existente.setContacto(request.getContacto() != null ? request.getContacto().trim() : null);
        existente.setTiempoEntrega(request.getTiempoEntrega());
        existente.setUsuarioActualizacion(request.getIdUsuario());

        Proveedor actualizado = proveedorRepository.save(existente);
        return convertirAProveedorResponse(actualizado);
    }

    // Conversión de Entidad Proveedor a ProveedorResponse DTO
    private ProveedorResponse convertirAProveedorResponse(Proveedor p) {
        return new ProveedorResponse(
                p.getIdProveedor(),
                p.getRuc(),
                p.getRazonSocial(),
                p.getContacto(),
                p.getTiempoEntrega(),
                p.getFechaCreacion(),
                p.getFechaActualizacion()
        );
    }
}
