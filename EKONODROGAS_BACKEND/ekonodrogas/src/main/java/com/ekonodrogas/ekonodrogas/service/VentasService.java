package com.ekonodrogas.ekonodrogas.service;

import com.ekonodrogas.ekonodrogas.dto.VentaCompletaDTO;
import com.ekonodrogas.ekonodrogas.dto.VentasDTO;
import com.ekonodrogas.ekonodrogas.persistence.DetalleVentasEntity;
import com.ekonodrogas.ekonodrogas.persistence.UsuariosEntity;
import com.ekonodrogas.ekonodrogas.persistence.VentasEntity;
import com.ekonodrogas.ekonodrogas.repository.DetalleVentasRepository;
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
    private final DetalleVentasRepository detalleVentasRepository;

    /**
     * NUEVO MÉTODO: Obtiene todas las ventas con sus detalles completos
     * Este es el método que debe usar el panel de administración
     */
    @Transactional(readOnly = true)
    public List<VentaCompletaDTO> obtenerTodasCompletas() {
        List<VentasEntity> ventas = ventasRepository.findAll();

        return ventas.stream()
                .map(this::entityToVentaCompletaDTO)
                .collect(Collectors.toList());
    }

    /**
     * NUEVO MÉTODO: Obtiene una venta completa por ID
     */
    @Transactional(readOnly = true)
    public VentaCompletaDTO obtenerCompletaPorId(Long id) {
        VentasEntity entity = ventasRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada con ID: " + id));
        return entityToVentaCompletaDTO(entity);
    }

    /**
     * NUEVO MÉTODO: Obtiene ventas completas de un usuario específico
     */
    @Transactional(readOnly = true)
    public List<VentaCompletaDTO> obtenerVentasCompletasPorUsuario(Long idUsuario) {
        List<VentasEntity> ventas = ventasRepository.findByUsuarioIdUsuario(idUsuario);

        return ventas.stream()
                .map(this::entityToVentaCompletaDTO)
                .collect(Collectors.toList());
    }

    // ========================================
    // MÉTODOS ORIGINALES (mantener compatibilidad)
    // ========================================

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

    // ========================================
    // MÉTODOS DE CONVERSIÓN
    // ========================================

    /**
     * NUEVO: Convierte Entity a VentaCompletaDTO con todos los detalles
     */
    private VentaCompletaDTO entityToVentaCompletaDTO(VentasEntity entity) {
        // Obtener todos los detalles de esta venta
        List<DetalleVentasEntity> detalles = detalleVentasRepository
                .findByVentaIdVenta(entity.getIdVenta());

        // Convertir cada detalle a DTO
        List<VentaCompletaDTO.DetalleProductoDTO> detallesDTO = detalles.stream()
                .map(detalle -> VentaCompletaDTO.DetalleProductoDTO.builder()
                        .idProducto(detalle.getProducto().getIdProducto())
                        .nombreProducto(detalle.getProducto().getNombreProducto())
                        .categoria(detalle.getProducto().getCategoria().getNombreCategoria())
                        .cantidad(detalle.getCantidad())
                        .precioUnitario(detalle.getPrecioUnitario())
                        .subtotal(detalle.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        // Calcular totales
        int cantidadProductosDiferentes = detalles.size();
        int cantidadTotalUnidades = detalles.stream()
                .mapToInt(DetalleVentasEntity::getCantidad)
                .sum();

        // Construir nombre completo del usuario
        UsuariosEntity usuario = entity.getUsuario();
        String nombreCompleto = String.format("%s %s %s %s",
                usuario.getPrimerNombre() != null ? usuario.getPrimerNombre() : "",
                usuario.getSegundoNombre() != null ? usuario.getSegundoNombre() : "",
                usuario.getPrimerApellido() != null ? usuario.getPrimerApellido() : "",
                usuario.getSegundoApellido() != null ? usuario.getSegundoApellido() : ""
        ).replaceAll("\\s+", " ").trim();

        return VentaCompletaDTO.builder()
                .idVenta(entity.getIdVenta())
                .idUsuario(entity.getUsuario().getIdUsuario())
                .nombreUsuario(nombreCompleto)
                .fechaVenta(entity.getFechaVenta())
                .totalVenta(entity.getTotalVenta())
                .estadoVenta(entity.getEstadoVenta().name())
                .detalles(detallesDTO)
                .cantidadProductosDiferentes(cantidadProductosDiferentes)
                .cantidadTotalUnidades(cantidadTotalUnidades)
                .build();
    }

    /**
     * Conversión simple Entity -> DTO (mantener para compatibilidad)
     */
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