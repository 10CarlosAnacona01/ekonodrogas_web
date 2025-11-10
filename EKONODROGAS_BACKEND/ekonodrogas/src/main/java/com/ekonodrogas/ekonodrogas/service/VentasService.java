package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.VentasDTO;
import com.ekonodrogas.ekonodrogas.persistence.UsuariosEntity;
import com.ekonodrogas.ekonodrogas.persistence.VentasEntity;
import com.ekonodrogas.ekonodrogas.repository.UsuariosRepository;
import com.ekonodrogas.ekonodrogas.repository.VentasRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VentasService {

    private final VentasRepository ventasRepository;
    private final UsuariosRepository usuariosRepository;

    @Transactional(readOnly = true)
    public List<VentasDTO> obtenerTodas() {
        return ventasRepository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VentasDTO obtenerPorId(Long id) {
        VentasEntity entity = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
        return entityToDto(entity);
    }

    @Transactional
    public VentasDTO crear(VentasDTO dto) {
        UsuariosEntity usuario = usuariosRepository.findById(dto.getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));

        VentasEntity.EstadoVenta estado;
        try {
            estado = VentasEntity.EstadoVenta.valueOf(dto.getEstadoVenta().toLowerCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Estado de venta inválido: " + dto.getEstadoVenta() +
                    ". Valores permitidos: completada, cancelada, pendiente");
        }

        VentasEntity entity = VentasEntity.builder()
                .usuario(usuario)
                .fechaVenta(LocalDateTime.now())
                .totalVenta(dto.getTotalVenta())
                .estadoVenta(estado)
                .build();

        VentasEntity guardada = ventasRepository.save(entity);
        return entityToDto(guardada);
    }

    @Transactional
    public VentasDTO actualizar(Long id, VentasDTO dto) {
        VentasEntity entity = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));

        if (dto.getIdUsuario() != null) {
            UsuariosEntity usuario = usuariosRepository.findById(dto.getIdUsuario())
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + dto.getIdUsuario()));
            entity.setUsuario(usuario);
        }

        if (dto.getEstadoVenta() != null) {
            try {
                VentasEntity.EstadoVenta estado = VentasEntity.EstadoVenta.valueOf(dto.getEstadoVenta().toLowerCase());
                entity.setEstadoVenta(estado);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Estado de venta inválido: " + dto.getEstadoVenta());
            }
        }

        entity.setTotalVenta(dto.getTotalVenta());

        VentasEntity actualizada = ventasRepository.save(entity);
        return entityToDto(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!ventasRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada con ID: " + id);
        }
        ventasRepository.deleteById(id);
    }

    // Conversión Entity <-> DTO
    private VentasDTO entityToDto(VentasEntity entity) {
        return VentasDTO.builder()
                .idVenta(entity.getIdVenta())
                .idUsuario(entity.getUsuario().getIdUsuario())
                .fechaVenta(entity.getFechaVenta())
                .totalVenta(entity.getTotalVenta())
                .estadoVenta(entity.getEstadoVenta().name())
                .build();
    }
}