package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.OfertasDTO;
import com.ekonodrogas.ekonodrogas.persistence.OfertasEntity;
import com.ekonodrogas.ekonodrogas.persistence.ProductosEntity;
import com.ekonodrogas.ekonodrogas.repository.OfertasRepository;
import com.ekonodrogas.ekonodrogas.repository.ProductosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfertasService {

    private final OfertasRepository ofertasRepository;
    private final ProductosRepository productosRepository;

    @Transactional(readOnly = true)
    public List<OfertasDTO> obtenerTodas() {
        return ofertasRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OfertasDTO obtenerPorId(Long id) {
        OfertasEntity entity = ofertasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada con ID: " + id));
        return entityToDto(entity);
    }

    @Transactional
    public OfertasDTO crear(OfertasDTO dto) {
        ProductosEntity producto = productosRepository.findById(dto.getIdProducto())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getIdProducto()));

        // Calcular descuento automáticamente
        Integer descuento = calcularDescuento(dto.getPrecioAnterior(), dto.getPrecioNuevo());

        OfertasEntity entity = OfertasEntity.builder()
                .idProducto(producto)
                .precioAnterior(dto.getPrecioAnterior())
                .precioNuevo(dto.getPrecioNuevo())
                .descuentoPorcentaje(descuento)
                .fechaCreacion(LocalDateTime.now())
                .build();

        OfertasEntity guardada = ofertasRepository.save(entity);
        return entityToDto(guardada);
    }

    @Transactional
    public OfertasDTO actualizar(Long id, OfertasDTO dto) {
        OfertasEntity entity = ofertasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada con ID: " + id));

        if (dto.getIdProducto() != null) {
            ProductosEntity producto = productosRepository.findById(dto.getIdProducto())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + dto.getIdProducto()));
            entity.setIdProducto(producto);
        }

        entity.setPrecioAnterior(dto.getPrecioAnterior());
        entity.setPrecioNuevo(dto.getPrecioNuevo());
        entity.setDescuentoPorcentaje(calcularDescuento(dto.getPrecioAnterior(), dto.getPrecioNuevo()));

        OfertasEntity actualizada = ofertasRepository.save(entity);
        return entityToDto(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!ofertasRepository.existsById(id)) {
            throw new RuntimeException("Oferta no encontrada con ID: " + id);
        }
        ofertasRepository.deleteById(id);
    }

    // Método auxiliar para calcular descuento
    private Integer calcularDescuento(Integer precioAnterior, Integer precioNuevo) {
        if (precioAnterior == null || precioNuevo == null || precioAnterior == 0) {
            return 0;
        }
        return (int) Math.round(((precioAnterior - precioNuevo) * 100.0) / precioAnterior);
    }

    // Toma la Entity y envía solo lo necesario DTO
    private OfertasDTO entityToDto(OfertasEntity entity) {
        return OfertasDTO.builder()
                .idOferta(entity.getIdOferta())
                .idProducto(entity.getIdProducto().getIdProducto())
                .precioAnterior(entity.getPrecioAnterior())
                .precioNuevo(entity.getPrecioNuevo())
                .descuentoPorcentaje(entity.getDescuentoPorcentaje())
                .fechaCreacion(entity.getFechaCreacion())
                .build();
    }
}
