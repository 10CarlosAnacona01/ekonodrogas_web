package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.ProductosDTO;
import com.ekonodrogas.ekonodrogas.persistence.CategoriasEntity;
import com.ekonodrogas.ekonodrogas.persistence.ProductosEntity;
import com.ekonodrogas.ekonodrogas.repository.CategoriasRepository;
import com.ekonodrogas.ekonodrogas.repository.ProductosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductosService {

    private final ProductosRepository productosRepository;
    private final CategoriasRepository categoriasRepository;

    @Transactional(readOnly = true)
    public List<ProductosDTO> obtenerTodos() {
        return productosRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ProductosDTO obtenerPorId(Long id) {
        ProductosEntity entity = productosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));
        return entityToDto(entity);
    }

    @Transactional
    public ProductosDTO crear(ProductosDTO dto) {
        CategoriasEntity categoria = categoriasRepository.findById(dto.getIdCategoria())
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getIdCategoria()));

        ProductosEntity entity = dtoToEntity(dto);
        entity.setCategoria(categoria);
        entity.setFechaCreacion(LocalDateTime.now());
        entity.setFechaActualizacion(LocalDateTime.now());

        ProductosEntity guardado = productosRepository.save(entity);
        return entityToDto(guardado);
    }

    @Transactional
    public ProductosDTO actualizar(Long id, ProductosDTO dto) {
        ProductosEntity entity = productosRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + id));

        if (dto.getIdCategoria() != null) {
            CategoriasEntity categoria = categoriasRepository.findById(dto.getIdCategoria())
                    .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + dto.getIdCategoria()));
            entity.setCategoria(categoria);
        }

        entity.setNombreProducto(dto.getNombreProducto());
        entity.setImagen(dto.getImagen());
        entity.setPrecio(dto.getPrecio());
        entity.setStock(dto.getStock());
        entity.setDisponible(dto.getDisponible());
        entity.setFechaActualizacion(LocalDateTime.now());

        ProductosEntity actualizado = productosRepository.save(entity);
        return entityToDto(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!productosRepository.existsById(id)) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productosRepository.deleteById(id);
    }

    // Métodos de conversión
    private ProductosDTO entityToDto(ProductosEntity entity) {
        return ProductosDTO.builder()
                .idProducto(entity.getIdProducto())
                .idCategoria(entity.getCategoria().getIdCategoria())
                .nombreProducto(entity.getNombreProducto())
                .imagen(entity.getImagen())
                .precio(entity.getPrecio())
                .stock(entity.getStock())
                .disponible(entity.getDisponible())
                .fechaCreacion(entity.getFechaCreacion())
                .fechaActualizacion(entity.getFechaActualizacion())
                .build();
    }

    private ProductosEntity dtoToEntity(ProductosDTO dto) {
        return ProductosEntity.builder()
                .idProducto(dto.getIdProducto())
                .nombreProducto(dto.getNombreProducto())
                .imagen(dto.getImagen())
                .precio(dto.getPrecio())
                .stock(dto.getStock())
                .disponible(dto.getDisponible())
                .build();
    }
}