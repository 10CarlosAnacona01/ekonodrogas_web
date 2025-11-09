package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.DetalleVentasDTO;
import com.ekonodrogas.ekonodrogas.persistence.DetalleVentasEntity;
import com.ekonodrogas.ekonodrogas.persistence.ProductosEntity;
import com.ekonodrogas.ekonodrogas.persistence.VentasEntity;
import com.ekonodrogas.ekonodrogas.repository.DetalleVentasRepository;
import com.ekonodrogas.ekonodrogas.repository.ProductosRepository;
import com.ekonodrogas.ekonodrogas.repository.VentasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DetalleVentasService {

    private final DetalleVentasRepository detalleVentasRepository;
    private final VentasRepository ventasRepository;
    private final ProductosRepository productosRepository;

    @Transactional(readOnly = true)
    public List<DetalleVentasDTO> obtenerTodos() {
        return detalleVentasRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DetalleVentasDTO obtenerPorId(Long id) {
        DetalleVentasEntity entity = detalleVentasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado con ID: " + id));
        return entityToDto(entity);
    }

    @Transactional
    public DetalleVentasDTO crear(DetalleVentasDTO dto) {
        VentasEntity venta = ventasRepository.findById(dto.getIdVenta())
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + dto.getIdVenta()));

        ProductosEntity producto = productosRepository.findById(dto.getProductos())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductos()));

        // Calcular subtotal automáticamente
        Integer subtotal = dto.getCantidad() * dto.getPrecioUnitario();

        DetalleVentasEntity entity = DetalleVentasEntity.builder()
                .venta(venta)
                .producto(producto)
                .cantidad(dto.getCantidad())
                .precioUnitario(dto.getPrecioUnitario())
                .subtotal(subtotal)
                .build();

        DetalleVentasEntity guardado = detalleVentasRepository.save(entity);
        return entityToDto(guardado);
    }

    @Transactional
    public DetalleVentasDTO actualizar(Long id, DetalleVentasDTO dto) {
        DetalleVentasEntity entity = detalleVentasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Detalle de venta no encontrado con ID: " + id));

        if (dto.getIdVenta() != null) {
            VentasEntity venta = ventasRepository.findById(dto.getIdVenta())
                    .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + dto.getIdVenta()));
            entity.setVenta(venta);
        }

        if (dto.getProductos() != null) {
            ProductosEntity producto = productosRepository.findById(dto.getProductos())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getProductos()));
            entity.setProducto(producto);
        }

        entity.setCantidad(dto.getCantidad());
        entity.setPrecioUnitario(dto.getPrecioUnitario());
        entity.setSubtotal(dto.getCantidad() * dto.getPrecioUnitario());

        DetalleVentasEntity actualizado = detalleVentasRepository.save(entity);
        return entityToDto(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!detalleVentasRepository.existsById(id)) {
            throw new RuntimeException("Detalle de venta no encontrado con ID: " + id);
        }
        detalleVentasRepository.deleteById(id);
    }

    // Conversión Entity <-> DTO
    private DetalleVentasDTO entityToDto(DetalleVentasEntity entity) {
        return DetalleVentasDTO.builder()
                .idDetalleVenta(entity.getIdDetalleVenta())
                .idVenta(entity.getVenta().getIdVenta())
                .productos(entity.getProducto().getIdProducto())
                .cantidad(entity.getCantidad())
                .precioUnitario(entity.getPrecioUnitario())
                .subtotal(entity.getSubtotal())
                .build();
    }
}