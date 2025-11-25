package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.CategoriasDTO;
import com.ekonodrogas.ekonodrogas.persistence.CategoriasEntity;
import com.ekonodrogas.ekonodrogas.repository.CategoriasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriasService {

    private final CategoriasRepository categoriasRepository;

    // Obtener las categorías
    // Es de solo lectura, no hace modificaciones en la DB
    @Transactional(readOnly = true)
    public List<CategoriasDTO> obtenerTodas() {
        return categoriasRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    // No se encuentra la categoría por su ID
    @Transactional(readOnly = true)
    public CategoriasDTO obtenerPorId(Long id) {
        CategoriasEntity entity = categoriasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));
        return entityToDto(entity);
    }

    // Crea una categoría
    @Transactional
    public CategoriasDTO crear(CategoriasDTO dto) {
        CategoriasEntity entity = dtoToEntity(dto);
        CategoriasEntity guardada = categoriasRepository.save(entity);
        return entityToDto(guardada);
    }

    // Actualiza una categoría
    @Transactional
    public CategoriasDTO actualizar(Long id, CategoriasDTO dto) {
        CategoriasEntity entity = categoriasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Categoría no encontrada con ID: " + id));

        entity.setNombreCategoria(dto.getNombreCategoria());
        CategoriasEntity actualizada = categoriasRepository.save(entity);
        return entityToDto(actualizada);
    }

    // Elimina una categoría
    @Transactional
    public void eliminar(Long id) {
        if (!categoriasRepository.existsById(id)) {
            throw new RuntimeException("Categoría no encontrada con ID: " + id);
        }
        categoriasRepository.deleteById(id);
    }

    // Métodos de conversión, devuelve un objeto DTO listo para usarse fuera del servicio, protege la DB, solo
    // expone datos necesarios al cliente
    private CategoriasDTO entityToDto(CategoriasEntity entity) {
        return CategoriasDTO.builder()
                .idCategoria(entity.getIdCategoria())
                .nombreCategoria(entity.getNombreCategoria())
                .build();
    }

    private CategoriasEntity dtoToEntity(CategoriasDTO dto) {
        return CategoriasEntity.builder()
                .idCategoria(dto.getIdCategoria())
                .nombreCategoria(dto.getNombreCategoria())
                .build();
    }
}