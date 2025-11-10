package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.RolesDTO;
import com.ekonodrogas.ekonodrogas.persistence.RolesEntity;
import com.ekonodrogas.ekonodrogas.repository.RolesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolesService {

    private final RolesRepository rolesRepository;

    @Transactional(readOnly = true)
    public List<RolesDTO> obtenerTodos() {
        return rolesRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RolesDTO obtenerPorId(Long id) {
        RolesEntity entity = rolesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));
        return entityToDto(entity);
    }

    @Transactional
    public RolesDTO crear(RolesDTO dto) {
        RolesEntity entity = dtoToEntity(dto);
        RolesEntity guardado = rolesRepository.save(entity);
        return entityToDto(guardado);
    }

    @Transactional
    public RolesDTO actualizar(Long id, RolesDTO dto) {
        RolesEntity entity = rolesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + id));

        entity.setNombreRol(dto.getNombreRol());
        RolesEntity actualizado = rolesRepository.save(entity);
        return entityToDto(actualizado);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!rolesRepository.existsById(id)) {
            throw new RuntimeException("Rol no encontrado con ID: " + id);
        }
        rolesRepository.deleteById(id);
    }

    // Métodos de conversión
    private RolesDTO entityToDto(RolesEntity entity) {
        return RolesDTO.builder()
                .idRol(entity.getIdRol())
                .nombreRol(entity.getNombreRol())
                .build();
    }

    private RolesEntity dtoToEntity(RolesDTO dto) {
        return RolesEntity.builder()
                .idRol(dto.getIdRol())
                .nombreRol(dto.getNombreRol())
                .build();
    }
}
